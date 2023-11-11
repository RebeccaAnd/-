package com.heima.reggie.common;

/*
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {    //主要目的是把 提示信息传进来
        super(message);
    }
}
