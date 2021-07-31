package com.wills.netty.chapter3_chat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王帅
 * @date 2021-07-31 00:35:51
 * @description:
 */
@Slf4j
public class MyIdleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = "";
            switch (event.state()){
                case WRITER_IDLE: eventType = "写空闲";break;
                case READER_IDLE: eventType = "读空闲";break;
                case ALL_IDLE: eventType = "读写空闲";break;
                default: eventType = "读写空闲"; break;
            }

            log.info(ctx.channel().remoteAddress() + "超时时间：" + eventType);

            // 关闭连接
            ctx.channel().close();
        }
    }
}
