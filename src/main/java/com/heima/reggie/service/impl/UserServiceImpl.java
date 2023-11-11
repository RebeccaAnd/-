package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.config.entity.User;
import com.heima.reggie.mapper.UserMapper;
import com.heima.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * + QQ邮箱验证方法拓展
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
