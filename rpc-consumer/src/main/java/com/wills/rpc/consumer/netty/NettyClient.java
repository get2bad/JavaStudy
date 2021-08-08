package com.wills.rpc.consumer.netty;

import com.wills.rpc.consumer.netty.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @ClassName NettyClient
 * @Date 2021/8/7 16:32
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Component
public class NettyClient implements InitializingBean, DisposableBean {

    private ClientHandler clientHandler;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    private NioEventLoopGroup client;

    public NettyClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run(String host, Integer port, ClientHandler handler){
        client = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(client)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new StringEncoder());
                            p.addLast(new StringDecoder());
                            p.addLast(handler);
                        }
                    });

            ChannelFuture sync = bootstrap.connect(host, port).sync();
            System.out.println("客户端启动完成！");
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            client.shutdownGracefully();
            System.out.println("客户端连接结束！");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void destroy() throws Exception {
        if(client != null)
            client.shutdownGracefully();
    }

    public Object send(String msg){
        clientHandler.setMsg(msg);
        Future future = executorService.submit(clientHandler);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
