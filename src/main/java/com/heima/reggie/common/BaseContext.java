package com.heima.reggie.common;

/*
 * 基于ThreadLocal封装工具类,用户用于保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>(); //每次调用都一个新线程,相当于以线程为作用域,每一个线程单独保存自己的副本
    //这段代码的作用是在每个线程中创建一个独立的 Long 类型的变量副本，可以在多线程环境中使用 threadLocal 变量，每个线程都可以独立地访问和修改自己的 threadLocal 变量副本，而不会相互干扰。

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
