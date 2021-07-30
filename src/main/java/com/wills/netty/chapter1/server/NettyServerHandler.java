package com.wills.netty.chapter1.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-07-30 13:55:48
 * @description:
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务器读取线程:" + Thread.currentThread().getName());
        System.out.println("server ctx:" + ctx);
        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(channel.remoteAddress() + "客户端：" + buf.toString(StandardCharsets.UTF_8));

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("欢迎客户端"+ ctx.channel().remoteAddress() +"来到聊天室",StandardCharsets.UTF_8));
//        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }
}
