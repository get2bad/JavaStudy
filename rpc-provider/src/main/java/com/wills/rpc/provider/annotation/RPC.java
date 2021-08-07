package com.wills.rpc.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 用于类上
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPC {
}
