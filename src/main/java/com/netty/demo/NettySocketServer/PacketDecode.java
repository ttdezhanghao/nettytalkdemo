package com.netty.demo.NettySocketServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName PacketDecode
 * @Description TODO
 * @Author Charlestang
 * @Date 8/25/2020 9:44
 * @Version 1.0
 **/
public class PacketDecode extends ByteToMessageDecoder {

    private LengthRule lengthRule = new LengthRule();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readableBytes();
        if (len <= LengthRule.LENGTH_FIELD_LENGTH) {
            return;
        }

        Object decode = lengthRule.decode(channelHandlerContext, byteBuf);
        if (decode != null) {
            list.add(decode);
        }
    }
}
