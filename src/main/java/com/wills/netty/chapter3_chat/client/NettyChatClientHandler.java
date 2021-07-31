package com.wills.netty.chapter3_chat.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 王帅
 * @date 2021-07-31 00:05:50
 * @description:
 */
public class NettyChatClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("发送消息：" + msg);
    }
}
