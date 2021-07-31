package com.wills.netty.chapter3_chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author 王帅
 * @date 2021-07-30 23:59:08
 * @description:
 */
public class NettyChatClient {

    private Integer port;
    private String host;

    public NettyChatClient() {

    }

    public NettyChatClient(String host,Integer port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new NettyChatClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()){
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg + "\r\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyChatClient client = new NettyChatClient("127.0.0.1",7777);
        client.run();
    }
}
