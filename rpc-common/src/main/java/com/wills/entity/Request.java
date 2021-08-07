package com.wills.entity;

import lombok.Data;

/**
 * @ClassName Request
 * @Date 2021/8/7 15:13
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Data
public class Request {

    // 请求对象id
    private String requestId;

    // 请求对象类名
    private String className;

    // 请求对象类下的方法名
    private String methodName;

    // 参数类型
    private Class<?>[] parameterTypes;

    // 入参
    private Object[] parameters;
}
