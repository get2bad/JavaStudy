package com.wills.netty.chapter5_tcp_coder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王帅
 * @date 2021-08-01 11:56:45
 * @description:
 * 隔壁老王牌自定义tcp协议
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WillsProtocol {
    // 关键点！用于对代表这个对象的字节长度，在服务器解析时会首先读取这个len，然后读取特定len长度的字节
    private int len;
    // 协议包的数据，指代要发送的数据本题
    private byte[] content;
}
