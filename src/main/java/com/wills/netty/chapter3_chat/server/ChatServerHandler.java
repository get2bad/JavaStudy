package com.wills.netty.chapter3_chat.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-07-30 23:34:59
 * @description:
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 发送消息
        Channel channel = ctx.channel();

        for (Channel ch : group) {
            if(ch == channel){
                // 发送给自己
                String finalMsg = "我说：" + msg + "\r\n";
                ByteBuf buf = Unpooled.copiedBuffer(finalMsg, StandardCharsets.UTF_8);
                ch.writeAndFlush(buf);
            } else {
                // 发送给别人
                String finalMsg = channel.remoteAddress().toString() + "说：" + msg + "\r\n";
                ByteBuf buf = Unpooled.copiedBuffer(finalMsg, StandardCharsets.UTF_8);
                ch.writeAndFlush(buf);
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        String msg = "服务器消息：客户端：【" + address.toString() + "】加入了聊天室！\r\n";
        group.writeAndFlush(msg);
        group.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        String msg = "服务器消息：客户端：【" + address.toString() + "】离开了聊天室！\r\n";
        group.writeAndFlush(msg);
//        group.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        String msg = "服务器消息：客户端：【" + address.toString() + "】上线了！\n";
        group.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        String msg = "服务器消息：客户端：【" + address.toString() + "】离线！\n";
        group.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        group.writeAndFlush("服务器遇到了一些错误，请您等待管理员解决！\n");
    }
}
