package com.heima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class}) //所以只要加了这两个注解的异常都会被处理
@ResponseBody //一会需要写一个方法,最终要返回封装的JSON
@Slf4j

public class GlobalExceptionHandler {
    /*
     * 进行异常处理方法
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //表示他处理这种异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在"; //动态获取用户名
            return R.error(msg);
        }

        return R.error("未知错误");
    }




}
