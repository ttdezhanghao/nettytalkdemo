package com.netty.demo;

import com.netty.demo.NettySocketServer.NettyClient;
import com.netty.demo.NettySocketServer.NettyServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        NettyServer nettyServer = new NettyServer();
        nettyServer.action();
        NettyClient nettyClient = new NettyClient();
        nettyClient.action();
    }
}
