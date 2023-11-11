package com.heima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 * 自定义元数据 对象处理器
 */

@Component //spring框架管理
@Slf4j //方便记录日志
public class MyMetaObjecthandler implements MetaObjectHandler {
    /*
     * 插入字段自动填充
     * 好处就是以后增加其他的也会自动填充,不用再手动设置
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        //如何获得当前用户ID
        //基与threadLocal的工具类,用于保护的获取当前登录用户Id
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /*
     * 更新字段自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
