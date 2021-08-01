package com.wills.netty.chapter5_tcp_coder.coder;

import com.wills.netty.chapter5_tcp_coder.entity.WillsProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 王帅
 * @date 2021-08-01 12:08:42
 * @description:
 */
@Slf4j
public class MyObjectDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readInt();
        byte[] content = new byte[len];
        in.readBytes(content);

        WillsProtocol protocol = new WillsProtocol(len,content);

        out.add(protocol);

        log.info("调用了解码方法");
    }
}
