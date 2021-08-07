package com.wills.entity.enum_code;

import lombok.Data;

/**
 * @ClassName HttpCode
 * @Date 2021/8/7 10:56
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
public enum HttpCode {

    OK(200,"请求完成"),ERROR(500,"服务器内部错误"),DEFAULT(504,"未知错误！");

    private Integer code;
    private String msg;

    HttpCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
