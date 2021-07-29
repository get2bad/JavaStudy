package com.wills.nio.example;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 王帅
 * @date 2021-07-29 12:24:58
 * @description:
 * 使用 NIO 进行读取文件
 */
public class ReadExample {

    public static void main(String[] args) throws Exception{
        File file = new File("/Users/wangshuai/Desktop/JavaStudy/file/nio.txt");

        FileInputStream in = new FileInputStream(file);

        FileChannel channel = in.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int)file.length());

        channel.read(buffer);

        System.out.println(new String(buffer.array()));

        in.close();
    }
}
