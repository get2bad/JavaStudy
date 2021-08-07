package com.wills.entity;

import com.alibaba.fastjson.JSON;
import com.wills.entity.enum_code.HttpCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Response
 * @Date 2021/8/7 10:55
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private Integer code;
    private Object data;
    private String msg;

    public static String ok(Object data){
        return JSON.toJSONString(new Response(HttpCode.OK.getCode(),data,HttpCode.OK.getMsg()));
    }

    public static String ok(){
        return JSON.toJSONString(new Response(HttpCode.OK.getCode(),null,HttpCode.OK.getMsg()));
    }

    public static String error(){
        return JSON.toJSONString(new Response(HttpCode.ERROR.getCode(),null,HttpCode.ERROR.getMsg()));
    }

    public static String msg(String msg){
        return JSON.toJSONString(new Response(HttpCode.OK.getCode(),null,msg));
    }
}
