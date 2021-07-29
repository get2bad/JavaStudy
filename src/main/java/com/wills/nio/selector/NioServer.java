package com.wills.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 王帅
 * @date 2021-07-29 13:15:08
 * @description:
 * 非阻塞 多selector 示例
 */
public class NioServer {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress(6666);

        Selector selector = Selector.open();

        serverSocketChannel.socket().bind(socketAddress);

        // 设置为 非阻塞的模型
        serverSocketChannel.configureBlocking(false);

        // 注册到 selector,关心的事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            if(selector.select(1000) == 0){
                // 没有事件发生
                System.out.println("服务器等待了1s，没有任何连接");
                continue;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    SocketChannel channel = serverSocketChannel.accept();
                    System.out.println("成功连接了一个channel" + channel);

                    // 设置非阻塞模型
                    channel.configureBlocking(false);

                    channel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()){
                    // 如果是可读取状态
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    socketChannel.read(buffer);

                    System.out.println("读取到了socket数据： " + new String(buffer.array()).trim());
                }
                iterator.remove();
            }
        }
    }
}
