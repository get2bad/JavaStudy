package com.wills.netty.chapter4_websocket.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author 王帅
 * @date 2021-07-31 20:20:16
 * @description:
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();

        for (Channel ch : group) {
            if(ch == channel){
                ch.writeAndFlush(new TextWebSocketFrame("我说:" + msg.text() + "\r\n"));
            } else {
                ch.writeAndFlush(new TextWebSocketFrame(ch.remoteAddress() + "说:" + msg.text() + "\r\n"));
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        group.add(ctx.channel());

        group.writeAndFlush(new TextWebSocketFrame("欢迎" + ctx.channel().remoteAddress() + "来到websocket聊天室！\r\n"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        group.remove(ctx.channel());

        group.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + "离开了websocket聊天室！\r\n"));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        group.writeAndFlush(new TextWebSocketFrame(ctx.channel() + "遇到了问题，被强制下线！\r\n"));
        group.remove(ctx.channel());
        cause.printStackTrace();
    }
}
