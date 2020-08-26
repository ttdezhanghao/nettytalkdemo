package com.netty.demo.NettySocketServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @ClassName LengthRule
 * @Description TODO
 * @Author Charlestang
 * @Date 8/25/2020 9:42
 * @Version 1.0
 **/
public class LengthRule extends  LengthFieldBasedFrameDecoder {
    public static final int MAX_FRAME_LENGTH = 32;
    public static final int LENGTH_FIELD_OFFSET = 1;
    public static final int LENGTH_FIELD_LENGTH = 1;
    public LengthRule() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }



    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
