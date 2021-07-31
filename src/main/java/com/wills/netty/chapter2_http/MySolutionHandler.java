package com.wills.netty.chapter2_http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-07-30 22:50:52
 * @description:
 */
public class MySolutionHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest){
            // 如果是http请求
            HttpRequest request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            String path = uri.getPath();
            if("/favicon.ico".equals(path)){
                System.out.println("请求了网站的图标， 不做响应");
                return ;
            }

            // 正常的请求
            ByteBuf buf = Unpooled.copiedBuffer("你好啊，网页客户端！".getBytes(StandardCharsets.UTF_8));
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain; Charset=UTF-8");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());

            ctx.writeAndFlush(httpResponse);
        }
    }
}
