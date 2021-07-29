package com.wills.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-07-29 13:24:22
 * @description:
 */
public class NioClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 6666);
        if(!socketChannel.connect(socketAddress)){
            // 如果连接不上
            while(!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会堵塞，可以做其他的工作");
            }
        }

        String str = "测试发送数据啦啦啦啦啦啦啦啦啦";
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);

        System.in.read();
    }
}
