package com.heima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * 配置mybatis plus的分页插件
 */
@Configuration
public class MybatisPlusConfig {

    @Bean //需要spring来管理他
    public MybatisPlusInterceptor mybatisPlusInterceptor() { //通过拦截器的方式把插件加进来
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); //加入插件(拦截器)
        return mybatisPlusInterceptor;
    }
}
