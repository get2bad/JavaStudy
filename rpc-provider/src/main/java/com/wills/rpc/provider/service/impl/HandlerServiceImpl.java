package com.wills.rpc.provider.service.impl;

import com.wills.api.HandlerService;
import com.wills.entity.Response;
import com.wills.entity.User;
import com.wills.rpc.provider.annotation.RPC;
import com.wills.rpc.provider.entity.Singleton;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName HandlerServiceImpl
 * @Date 2021/8/7 11:48
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@RPC
@Service
public class HandlerServiceImpl implements HandlerService {


    @Override
    public String addUser(User user) {
        List<User> instance = Singleton.getUserInstance();
        instance.add(user);
        return Response.ok(user);
    }

    @Override
    public String getAllUser() {
        List<User> instance = Singleton.getUserInstance();
        return Response.ok(instance);
    }
}
