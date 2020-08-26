package com.netty.demo.NettySocketServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @ClassName MyEncode
 * @Description TODO
 * @Author Charlestang
 * @Date 8/24/2020 13:35
 * @Version 1.0
 **/
public class MyEncode extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String str, ByteBuf byteBuf) throws Exception {
        if (Objects.isNull(str)) {
            throw new RuntimeException("encode msg null");
        }
        byte[] bytes = str.getBytes();
        byteBuf.writeByte(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
