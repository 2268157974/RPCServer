package com.lch.rpc.socket;

import com.google.gson.Gson;
import com.lch.rpc.mode.Request;
import com.lch.rpc.util.Logg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * 0         1        4
 * +--------+---------+
 * |  type | length  |
 * +-----------------+
 * |                 |
 * |     data        |
 * |                 |
 * |                 |
 * +-----------------+
 * 1B  type（消息类型）   4B length（总长度）
 * data（数据）
 */
public class RPCDecoder extends ByteArrayDecoder {
    private String TAG;
    private Gson mGson = new Gson();

    public RPCDecoder(String name) {
        TAG = name + "_DECODER";
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte type = msg.readByte();
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        ByteBuf data = msg.readBytes(bytes, 0, length);
        Request request = mGson.fromJson(new String(bytes), Request.class);
        ReferenceCountUtil.release(msg);
        Logg.d(TAG, "REQUEST IS " + request);
    }
}
