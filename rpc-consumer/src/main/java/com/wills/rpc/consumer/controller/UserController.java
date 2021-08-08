package com.wills.rpc.consumer.controller;

import com.wills.api.HandlerService;
import com.wills.entity.User;
import com.wills.rpc.consumer.annotation.RpcReturn;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @ClassName UserController
 * @Date 2021/8/7 17:54
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReturn
    private HandlerService service;

    @GetMapping("list")
    public String list(){
        String res = service.getAllUser();
        return res;
    }

    @GetMapping("add")
    public String add(String name,Integer age){
        Random r = new Random();
        User user = new User(r.nextInt(1000),name,age);
        String res = service.addUser(user);
        return res;
    }
}
