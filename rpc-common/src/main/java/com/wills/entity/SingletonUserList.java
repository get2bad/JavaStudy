package com.wills.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SingletonUserMap
 * @Date 2021/8/7 11:48
 * @Author 王帅
 * @Version 1.0
 * @Description
 * 单例模式 - 用户list集合
 */
public class SingletonUserList {

    private static List<User> data;

    public synchronized List<User> getInstance(){
        if(data == null){
            synchronized (this){
                data = new ArrayList<>();
            }
        }
        return data;
    }
}
