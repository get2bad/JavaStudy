package com.wills.nio.example.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 王帅
 * @date 2021-07-29 13:54:15
 * @description:
 *
 * 群聊 NIO 非阻塞模型 示例
 */
public class NioChatGroupServer {


    private final Integer PORT = 7777;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public NioChatGroupServer() {
        try{
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            // 非阻塞模型
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();

            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void listen()  throws Exception{
        int count = selector.select();
        if(count > 0){
            // 如果
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    SocketChannel accept = serverSocketChannel.accept();
                    String msg = accept.getRemoteAddress() +"连接了聊天室\r\n";
                    System.out.println(msg);

                    // 设置非阻塞模型
                    accept.configureBlocking(false);

                    // 注册一下，然后监听的是 读取事件！
                    accept.register(selector,SelectionKey.OP_READ);

                    sendToOtherClient(msg,accept);
                }

                if(key.isReadable()){
                    readMsg(key);
                }

                iterator.remove();
            }
        } else {
            System.out.println("正在等待连接....");
        }
    }

    public void readMsg(SelectionKey key){
        SocketChannel channel = null;
        try{
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int read = channel.read(buffer);
            if(read > 0){
                String msg = new String(buffer.array()).trim();
                System.out.println("=====> 服务器收到来自"+ channel +"的消息：" + msg);

                // 发送到其他的客户端
                try {
                    sendToOtherClient(channel.getRemoteAddress().toString() + ":" + msg + "\r\n",channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (IOException e){
            try{
                System.out.println(channel.getRemoteAddress() + "离线了");
                //取消注册
                key.cancel();
                channel.close();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    public void sendToOtherClient(String msg,SocketChannel self) throws Exception{
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();

            if(channel instanceof SocketChannel && channel != self){
                SocketChannel dist = (SocketChannel) channel;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
                dist.write(buffer);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        NioChatGroupServer server = new NioChatGroupServer();
        while(true){
            server.listen();
        }
    }
}
