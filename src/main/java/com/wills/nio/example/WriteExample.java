package com.wills.nio.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @date 2021-07-29 12:25:24
 * @description:
 * 使用 NIO 进行 写入文件操作
 */
public class WriteExample {

    public static void main(String[] args) throws Exception {
        String str = "开始写入文件啊啊啊啊啊啊啊！这是NIO写入";
        FileOutputStream in = new FileOutputStream("/Users/wangshuai/Desktop/JavaStudy/file/nio.txt");

        FileChannel channel = in.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(str.getBytes(StandardCharsets.UTF_8));

        // 从写状态变为 读状态
        buffer.flip();

        channel.write(buffer);

        // 关闭流
        in.close();

    }
}
