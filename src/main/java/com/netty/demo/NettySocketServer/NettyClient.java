package com.netty.demo.NettySocketServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author Charlestang
 * @Date 8/24/2020 10:22
 * @Version 1.0
 **/
public class NettyClient {

    private final static Logger log = LoggerFactory.getLogger(NettyClient.class);

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private int port = 8088;/*default 8088*/

    private String ip = "127.0.0.1";/*default 127.0.0.1*/

    public NettyClient() {
    }

    public NettyClient(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public void action() throws InterruptedException {
        EventLoopGroup bossEvent = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossEvent)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //socketChannel.pipeline().addLast(new PacketDecode());
                        //socketChannel.pipeline().addLast(new MyEncode());
                        //socketChannel.pipeline().addLast(new MyDecode());
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(1));
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ListenAndTalk.MAXLEN, 0, 1,0,1));
                        socketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                        socketChannel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                        socketChannel.pipeline().addLast(new MyClientHandler());
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(ip, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("netty client started -----------------------------------------");
            } else {
                log.info("netty client start failed -----------------------------------------");
            }
        });
        //log.info("netty client started ------------------------------");
        //channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer());
        //channelFuture.channel().closeFuture().sync();
    }

    public class MyClientHandler extends SimpleChannelInboundHandler<String> {

        int index = 0;
        int index1 = 0;

        public MyClientHandler() {
            super();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("server connections to client succeed");
            //super.channelActive(ctx);
            executorService.execute(() -> {
                for (int i = 0; i < 10 * 10000; i++) {
                    //log.info("第 {} 次胡同口嘲讽", i);
                    ctx.write(ListenAndTalk.QUESTION2);
                    if(i%ListenAndTalk.BATCHSIZE == 0){
                        ctx.flush();
                    }
                    //log.info("李大爷说: {}", ListenAndTalk.QUESTION2);
                }
            });

        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            if (ListenAndTalk.QUESTION1.equals(msg)) {
                index1++;
                ctx.write(ListenAndTalk.ANSWER1);
                if(index1%ListenAndTalk.BATCHSIZE == 0){
                    ctx.flush();
                }
                //log.info("李大爷回答;{}", ListenAndTalk.ANSWER1);

            } else if (ListenAndTalk.ANSWER2.equals(msg)) {
                index++;
                ctx.write(ListenAndTalk.QUESTION3);
                if(index%ListenAndTalk.BATCHSIZE == 0){
                    ctx.flush();
                }
                //log.info("李大爷问;{}", ListenAndTalk.QUESTION3);

            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //log.info("messages from sever read finish");
            super.channelReadComplete(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }
}
