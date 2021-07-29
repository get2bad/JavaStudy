package com.wills.nio.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author 王帅
 * @date 2021-07-29 12:44:58
 * @description:
 * 拷贝文件
 */
public class TransferExample {


    public static void main(String[] args) throws Exception {
        FileInputStream in = new FileInputStream("/Users/wangshuai/Desktop/JavaStudy/file/nio.txt");
        FileOutputStream out = new FileOutputStream("/Users/wangshuai/Desktop/JavaStudy/file/tansfer.txt");

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();


        inChannel.transferTo(0,inChannel.size(),outChannel);

        outChannel.close();
        inChannel.close();
        out.close();
        in.close();
    }
}
