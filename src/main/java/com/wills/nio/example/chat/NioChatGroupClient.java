package com.wills.nio.example.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author 王帅
 * @date 2021-07-29 14:54:43
 * @description:
 */
public class NioChatGroupClient {

    private final Integer PORT = 7777;
    private final String HOST = "127.0.0.1";
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public NioChatGroupClient() throws Exception{
        selector = Selector.open();

        socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));

        socketChannel.configureBlocking(false);

        // 注册selector,监听读取事件
        socketChannel.register(selector, SelectionKey.OP_READ);

        userName = socketChannel.getRemoteAddress().toString();
    }

    public void sendSay(String msg){
        msg = userName + ":" + msg + "\r\n";

        try{
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void receiveSay(){
        try{
            int count = selector.select();
            if(count > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        channel.read(buffer);

                        String msg = new String(buffer.array()).trim();
                        System.out.println(msg);
                    }
                }
                iterator.remove();
            } else {
//                System.out.println();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        NioChatGroupClient client = new NioChatGroupClient();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    client.receiveSay();
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String msg = scanner.nextLine();
            client.sendSay(msg);
        }
    }
}
