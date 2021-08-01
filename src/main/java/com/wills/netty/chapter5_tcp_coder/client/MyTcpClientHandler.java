package com.wills.netty.chapter5_tcp_coder.client;

import com.wills.netty.chapter5_tcp_coder.entity.WillsProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-08-01 12:24:17
 * @description:
 */
@Slf4j
public class MyTcpClientHandler extends SimpleChannelInboundHandler<WillsProtocol> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WillsProtocol msg) throws Exception {
        log.info("客户端收到消息：" + new String(msg.getContent(),StandardCharsets.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道就绪，就进行发送数据

        for (int i = 0; i < 10; i++) {
            byte[] content = (i + ":测试发送信息，解决TCP粘包/拆包问题").getBytes(StandardCharsets.UTF_8);
            int len = content.length;
            WillsProtocol protocol = new WillsProtocol(len, content);
            ctx.writeAndFlush(protocol);
        }
    }
}
