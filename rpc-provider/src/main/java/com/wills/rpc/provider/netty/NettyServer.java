package com.wills.rpc.provider.netty;

import com.wills.rpc.provider.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName NettyServer
 * @Date 2021/8/7 11:12
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Slf4j
@Component
public class NettyServer implements DisposableBean {

    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;



    public void run(String host,Integer port,ServerHandler serverHandler) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加String（json）处理器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(serverHandler);
                        }
                    });
            ChannelFuture future = bootstrap.bind(host, port).sync();
            System.out.println("开启server-RPC成功！");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void destroy() throws Exception {
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }

        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }
}
