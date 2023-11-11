package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.config.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
