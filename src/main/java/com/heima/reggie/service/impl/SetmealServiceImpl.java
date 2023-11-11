package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.common.CustomException;
import com.heima.reggie.config.entity.Setmeal;
import com.heima.reggie.config.entity.SetmealDish;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.mapper.SetmealMapper;
import com.heima.reggie.service.SetmealDishService;
import com.heima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /*
     * 新增套餐 ,同时保存菜品和套餐关联关系
     */
    @Transactional  //因为涉及两张表,注解一致性 全成功/失败
    public void saveWithDish(SetmealDto setmealDto) {
        //1 保存套餐基本信息(setmeal) insert
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();  //依旧,实体集合中的属性只有部分有值,setmealId没有值
        setmealDishes.stream().map((item)->{    //有的属性没有值,把集合中的依次取出来赋值
            item.setSetmealId(setmealDto.getId());
            return item;
                }).collect(Collectors.toList());
        //2 保存套餐和菜品的关联信息(setmeal_dish) insert
        setmealDishService.saveBatch(setmealDishes);

    }

    /*
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //先查询套餐状态-确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            //条件
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        long count = this.count(queryWrapper);   //ServiceImpl

        //如果不能删除,抛出一个业务异常
        if(count > 0) {
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        //如果可以删除,先删除套餐表中的数据(setmeal
        this.removeByIds(ids);

        //删除关系表中的数据(setmeal_dish
//        setmealDishService.removeByIds(ids);    //传入的id不是这个表里面的主键值,不能直接这么调用
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);    //嵌套查询

    }
}
