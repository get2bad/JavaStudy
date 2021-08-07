package com.wills.api;

import com.wills.entity.User;

/**
 * @ClassName HandlerService
 * @Date 2021/8/7 11:45
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
public interface HandlerService {

    public String addUser(User user);

    public String getAllUser();
}
