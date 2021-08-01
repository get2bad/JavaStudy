package com.wills.netty.chapter5_tcp_coder.server;

import com.wills.netty.chapter5_tcp_coder.coder.MyObjectDecoder;
import com.wills.netty.chapter5_tcp_coder.coder.MyObjectEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author 王帅
 * @date 2021-08-01 12:11:24
 * @description:
 */
public class TcpNettyServer {

    private Integer port;

    public TcpNettyServer() {
    }

    public TcpNettyServer(Integer port) {
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup client = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss,client)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new MyObjectEncoder());
                            p.addLast(new MyObjectDecoder());
                            p.addLast(new MyTcpServerHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(port).sync();
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            client.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        TcpNettyServer server = new TcpNettyServer(7777);
        server.run();
    }
}
