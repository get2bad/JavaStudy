package com.wills.rpc.consumer;

import com.wills.rpc.consumer.netty.NettyClient;
import com.wills.rpc.consumer.netty.handler.ClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcConsumerApplication implements CommandLineRunner {

    @Value("${socket.host}")
    private String host;

    @Value("${socket.port}")
    private Integer port;

    @Autowired
    private ClientHandler handler;

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NettyClient(handler).run(host,port,handler);
            }
        }).start();
    }
}
