package com.wills.netty.chapter5_tcp_coder.client;

import com.wills.netty.chapter5_tcp_coder.coder.MyObjectDecoder;
import com.wills.netty.chapter5_tcp_coder.coder.MyObjectEncoder;
import com.wills.netty.chapter5_tcp_coder.server.TcpNettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王帅
 * @date 2021-08-01 12:19:40
 * @description:
 */
@Slf4j
public class TcpNettyClient {

    private Integer port;
    private String host;

    public TcpNettyClient() {
    }

    public TcpNettyClient(Integer port, String host) {
        this.port = port;
        this.host = host;
    }

    public void run(){
        NioEventLoopGroup client = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(client)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyObjectEncoder());
                            pipeline.addLast(new MyObjectDecoder());
                            pipeline.addLast(new MyTcpClientHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            client.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        TcpNettyClient client = new TcpNettyClient(7777, "127.0.0.1");

        client.run();
    }
}
