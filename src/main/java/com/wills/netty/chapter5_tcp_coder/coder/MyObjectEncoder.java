package com.wills.netty.chapter5_tcp_coder.coder;

import com.wills.netty.chapter5_tcp_coder.entity.WillsProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;


/**
 * @author 王帅
 * @date 2021-08-01 11:55:48
 * @description:
 */
@Slf4j
public class MyObjectEncoder extends MessageToByteEncoder<WillsProtocol> {


    @Override
    protected void encode(ChannelHandlerContext ctx, WillsProtocol msg, ByteBuf out) throws Exception {
        log.info("编码方法被调用了！");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
