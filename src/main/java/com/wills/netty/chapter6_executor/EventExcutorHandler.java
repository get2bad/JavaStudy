package com.wills.netty.chapter6_executor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @ClassName EventExcutorHandler
 * @Date 2021/8/3 13:54
 * @Author 王帅
 * @Version 1.0
 * @Description
 * 实验使用线程池在channelRead中进行异步操作
 */
public class EventExcutorHandler extends SimpleChannelInboundHandler<Object> {

    private final EventExecutorGroup group = new DefaultEventLoopGroup(16);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        group.submit(() -> {
            ByteBuf buf = (ByteBuf) msg;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            try {
                String dataStr = new String(data,"utf-8");
                Thread.sleep(10 * 1000);
                ctx.channel().writeAndFlush(dataStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
