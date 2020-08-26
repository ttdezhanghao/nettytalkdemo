package com.netty.demo.NettySocketServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MyDecode
 * @Description TODO
 * @Author Charlestang
 * @Date 8/24/2020 13:34
 * @Version 1.0
 **/
public class MyDecode extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readableBytes() & 0xff;
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        /*if (bytes.length > 0) {
            list.add(new String(Arrays.copyOfRange(bytes, 1, bytes.length)));
        }*/
        list.add(new String(bytes, StandardCharsets.UTF_8));
    }
}
