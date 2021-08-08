package com.wills.rpc.consumer.netty.handler;

import com.wills.rpc.consumer.RpcConsumerApplication;
import com.wills.rpc.consumer.netty.NettyClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @ClassName ClientHandler
 * @Date 2021/8/7 16:38
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Component
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    private ChannelHandlerContext ctx;

    private String msg;

    private String respMsg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        respMsg = msg;
        notify();
    }

    /**
     * 给服务端发送消息，拿到这个channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务器失去连接！启动重新连接进程");
        ctx.close();
        doReconnect();
    }

    public void doReconnect() throws Exception {
        // 重连方法
    }

    @Override
    public synchronized Object call() throws Exception {
        ctx.writeAndFlush(msg);
        wait();
        return respMsg;
    }
}
