package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.config.entity.OrderDetail;
import com.heima.reggie.mapper.OrderDetailMapper;
import com.heima.reggie.service.AddressBookService;
import com.heima.reggie.service.OrderDetailService;
import com.heima.reggie.service.ShoppingCartService;
import com.heima.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}

