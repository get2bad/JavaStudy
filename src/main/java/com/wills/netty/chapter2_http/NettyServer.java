package com.wills.netty.chapter2_http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author 王帅
 * @date 2021-07-30 22:44:35
 * @description:
 */
public class NettyServer {

    private static final Integer PORT = 7777;

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup clientGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,clientGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("MyHttpServerCodec",new HttpServerCodec())
                                    .addLast("MySolutionHandler", new MySolutionHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
