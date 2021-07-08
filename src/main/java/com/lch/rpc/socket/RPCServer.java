package com.lch.rpc.socket;

import com.lch.rpc.util.Logg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RPCServer implements Runnable {
    private static final String TAG = "RPCSERVER";
    private static final int PORT = 9090;
    private NioEventLoopGroup mServerGroup;
    private NioEventLoopGroup mWorkGroup;
    private ServerBootstrap mBootstrap;
    private ScheduledThreadPoolExecutor mExecutor;
    private Channel mChannel;

    private RPCServer() {
    }

    private void init() {
        Logg.d(TAG, "SERVER START ...");
        mServerGroup = new NioEventLoopGroup();
        mWorkGroup = new NioEventLoopGroup();
        mBootstrap = new ServerBootstrap();
        mBootstrap.group(mServerGroup, mWorkGroup)
                .channel(NioServerSocketChannel.class)
                //socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值 windows为200，其它为128。
                .option(ChannelOption.SO_BACKLOG, 128)
                //TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。
                //该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
                //如果需要发送一些较小的报文，则需要禁用该算法。
                //Netty默认禁用该算法，从而最小化报文传输延时。
                .childOption(ChannelOption.TCP_NODELAY, true)
                //Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                //可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。
                //Netty默认关闭该功能。
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RPCDecoder(TAG))
                                .addLast(new RPCEncoder())
                                .addLast(new RPCHandler());
                    }
                });
        try {
            mChannel = mBootstrap.bind(PORT).sync().channel();
            Logg.d(TAG, "SERVER START SUCCESS");
            mChannel.closeFuture().sync();
            Logg.d(TAG, "SERVER STOP SUCCESS");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logg.d(TAG, "SERVER ERROR AND RETRY AFTER 2s");
            mExecutor.schedule(this, 2, TimeUnit.SECONDS);
        }
    }

    private void start() {
        mExecutor = new ScheduledThreadPoolExecutor(1, new IThreadFactory(TAG));
        mExecutor.schedule(this, 0, TimeUnit.MILLISECONDS);
    }

    public static void startServer() {
        new RPCServer().start();
    }

    @Override
    public void run() {
        init();
    }

    private static class IThreadFactory implements ThreadFactory {
        private final String mName;

        IThreadFactory(String name) {
            mName = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, mName);
            Logg.d(TAG, "Created a new Thread ,name is " + mName + ",ID is " + thread.getId());
            return thread;
        }
    }
}
