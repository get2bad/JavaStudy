package com.wills.netty.chapter4_websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author 王帅
 * @date 2021-07-31 20:12:44
 * @description:
 */
public class NettyServer {

    private Integer port;

    public NettyServer(Integer port) {
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup child = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss,child)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true) // 保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            // 以块的方式写入
                            pipeline.addLast(new ChunkedWriteHandler());

                            // 聚合http
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
                            pipeline.addLast(new MyWebSocketHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            child.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        NettyServer server = new NettyServer(7777);
        server.run();
    }
}
