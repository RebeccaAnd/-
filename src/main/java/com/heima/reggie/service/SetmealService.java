package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.config.entity.Setmeal;
import com.heima.reggie.dto.SetmealDto;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /*
     * 新增套餐 ,同时保存菜品和套餐关联关系
     */
    public void saveWithDish(SetmealDto setmealDto);

    /*
     * 删除套餐,同时删除套餐和菜品的关联数据
     */
    public void removeWithDish(List<Long> ids);

}
