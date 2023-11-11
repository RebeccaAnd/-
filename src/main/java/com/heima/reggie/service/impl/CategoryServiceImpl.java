package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.common.CustomException;
import com.heima.reggie.config.entity.Category;
import com.heima.reggie.config.entity.Dish;
import com.heima.reggie.config.entity.Setmeal;
import com.heima.reggie.mapper.CategoryMapper;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.DishService;
import com.heima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //注入需要的分类Service
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /*
     * 根据id删除分类;删除之前进行判断(分类是否关联菜品/套餐)
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(dishLambdaQueryWrapper);

        //分类是否关联菜品,如果已经关联,抛出一个业务异常
        if (count1 > 0) {   //待定,先构建框架
            throw new CustomException("当前分类下关联了菜品,不能删除");   //提示信息最终希望在页面上看到 -> 全局异常处理的地方来完成
        }

        //分类是否关联套餐,如果已经关联,抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count();

        if (count2 > 0) {   //待定,先构建框架
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }


        //正常删除分类
        super.removeById(id);
    }
}
