package com.lch.rpc.socket;

import io.netty.handler.codec.bytes.ByteArrayEncoder;

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
public class RPCEncoder extends ByteArrayEncoder {
}
