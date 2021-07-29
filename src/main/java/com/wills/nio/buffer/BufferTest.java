package com.wills.nio.buffer;

import java.nio.IntBuffer;

/**
 * @author 王帅
 * @date 2021-07-29 11:31:55
 * @description:
 */
public class BufferTest {

    public static void main(String[] args) {
        IntBuffer in = IntBuffer.allocate(5);


        for (int i = 0; i < in.capacity(); i++) {
            in.put(i * 2);
        }

        // 转换状态，将写状态转换为读状态
        in.flip();

        while(in.hasRemaining()){
            System.out.println(in.get());
        }

        // 转换状态，将读状态转换为写状态
        in.flip();

        for (int i = 0; i < in.capacity(); i++) {
            in.put(i * 2);
        }

        // 转换状态，将写状态转换为读状态
        in.flip();

        while(in.hasRemaining()){
            System.out.println(in.get());
        }
    }
}
