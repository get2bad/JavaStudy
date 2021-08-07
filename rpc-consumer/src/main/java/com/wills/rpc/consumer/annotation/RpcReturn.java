package com.wills.rpc.consumer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName RpcReturn
 * @Date 2021/8/7 17:52
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
// 作用于字段
@Target(ElementType.FIELD)
// 运行时可见
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReturn {
}
