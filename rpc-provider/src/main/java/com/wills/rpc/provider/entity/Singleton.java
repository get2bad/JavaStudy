package com.wills.rpc.provider.entity;

import com.wills.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName SingletonUserMap
 * @Date 2021/8/7 11:48
 * @Author 王帅
 * @Version 1.0
 * @Description
 * 单例模式 - 用户list集合
 */
public class Singleton {

    private static AtomicReference<List<User>> userList = new AtomicReference<>();

    private static AtomicReference<Map<String,Object>> beanMap = new AtomicReference<>();

    public static List<User> getUserInstance(){
        for (;;){
            List<User> users = userList.get();
            if(null != users) return users;
            userList.compareAndSet(null, new ArrayList<>());
            return userList.get();
        }
    }

    public static Map<String,Object> getBeanInstance(){
        // 自旋锁的方式 获取单例对象
        for (;;){
            Map<String, Object> beans = beanMap.get();
            if(null != beans) return beans;
            beanMap.compareAndSet(null, new HashMap<>());
            return beanMap.get();
        }
    }
}
