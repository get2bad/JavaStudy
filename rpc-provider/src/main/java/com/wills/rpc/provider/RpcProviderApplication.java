package com.wills.rpc.provider;

import com.wills.rpc.provider.netty.NettyServer;
import com.wills.rpc.provider.netty.handler.ServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcProviderApplication implements CommandLineRunner {

    @Value("${socket.port}")
    private Integer port;

    @Value("${socket.host}")
    private String host;

    @Autowired
    private ServerHandler serverHandler;

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NettyServer().run(host, port,serverHandler);
            }
        }).start();
    }
}
