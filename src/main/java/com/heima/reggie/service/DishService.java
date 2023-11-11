package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.config.entity.Dish;
import com.heima.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据,要在两张表中插入数据
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息 和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息,同时更新对应口味
    public void updateWithFlavor(DishDto dishDto);
}
