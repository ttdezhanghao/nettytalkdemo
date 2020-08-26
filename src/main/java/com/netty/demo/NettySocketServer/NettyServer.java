package com.netty.demo.NettySocketServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName NettyServer
 * @Description TODO
 * @Author Charlestang
 * @Date 8/24/2020 10:22
 * @Version 1.0
 **/
public class NettyServer {

    private final static Logger log = LoggerFactory.getLogger(NettyServer.class);
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private int port = 8088;/*default 8088*/

    public NettyServer() {
    }

    public NettyServer(int port) {
        this.port = port;
    }

    public void action() {
        EventLoopGroup bossEvent = new NioEventLoopGroup();
        EventLoopGroup workEvent = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEvent, workEvent)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler())
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //socketChannel.pipeline().addLast(new PacketDecode());
                            //socketChannel.pipeline().addLast(new MyEncode());//encode
                            //socketChannel.pipeline().addLast(new MyDecode());//decode
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(1));
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ListenAndTalk.MAXLEN, 0, 1,0,1));
                            socketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                            socketChannel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                            socketChannel.pipeline().addLast(new MyServerHandler());//do handler
                        }
                    });
            serverBootstrap.bind(port).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("netty server started -----------------------------------------");
                } else {
                    log.info("netty server start failed -----------------------------------------");
                }
            });
            //channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            log.error("something error: {}", ex.getMessage());
        } /*finally {
            bossEvent.shutdownGracefully();
            workEvent.shutdownGracefully();
        }*/
    }

    public class MyServerHandler extends SimpleChannelInboundHandler<String> {

        StopWatch stopWatch = new StopWatch("耗时统计");
        int index = 0;
        int index1 = 0;

        public MyServerHandler() {
            super();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            if (ListenAndTalk.QUESTION2.equals(msg)) {
                index1++;
                ctx.write(ListenAndTalk.ANSWER2);
                if(index1%ListenAndTalk.BATCHSIZE == 0){
                    ctx.flush();
                }
                //log.info("张大爷回答;{}", ListenAndTalk.ANSWER2);


            } else if (ListenAndTalk.QUESTION3.equals(msg)) {
                index++;
                ctx.write(ListenAndTalk.ANSWER3);
                if(index%ListenAndTalk.BATCHSIZE == 0){
                    ctx.flush();
                }
                //log.info("张大爷回答;{}", ListenAndTalk.ANSWER3);
                if(index == 10 * 10000-1){
                    stopWatch.stop();
                    System.out.println(stopWatch.getTotalTimeSeconds());
                }

            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("server connections to client succeed");
            //super.channelActive(ctx);
            stopWatch.start();
            executorService.execute(() -> {
                for (int i = 0; i < 10 * 10000; i++) {
                    //log.info("第 {} 次胡同口嘲讽", i);
                    ctx.write(ListenAndTalk.QUESTION1);
                    if(i%ListenAndTalk.BATCHSIZE == 0){
                        ctx.flush();
                    }
                    //log.info("张大爷说: {}", ListenAndTalk.QUESTION1);
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //log.info("messages from client read finish");
            super.channelReadComplete(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }
}
