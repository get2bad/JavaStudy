package com.wills.netty.chapter5_tcp_coder.server;

import com.wills.netty.chapter5_tcp_coder.entity.WillsProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author 王帅
 * @date 2021-08-01 12:19:01
 * @description:
 */
@Slf4j
public class MyTcpServerHandler extends SimpleChannelInboundHandler<WillsProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WillsProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();

        log.info("服务器收到消息：" + new String(content, StandardCharsets.UTF_8));

        // 回复消息
        byte[] replyContent = UUID.randomUUID().toString().replace("-","").getBytes(StandardCharsets.UTF_8);
        int replayLen = replyContent.length;

        WillsProtocol protocol = new WillsProtocol(replayLen,replyContent);

        ctx.writeAndFlush(protocol);
    }
}
