package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.config.entity.Dish;
import com.heima.reggie.config.entity.DishFlavor;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.mapper.DishMapper;
import com.heima.reggie.service.DishFlavorService;
import com.heima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /*
     * 新增菜品插入数据,包括 保存对应的口味
     */
    @Transactional  //因为涉及到多张表的操作-事务控制 (保证数据的一致性)
    public void saveWithFlavor(DishDto dishDto){
        //保存菜品的基本信息到dish
        this.save(dishDto); //因为是继承dish的

        Long dishId = dishDto.getId();  //菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {   //stream 遍历出来每个item都是一个实体类
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());    //又转成了一个List,里面的元素加工了一下

        //保存菜品口味数据到dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /*
     * 根据id查询菜品信息和对应的口味信息
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //1查询菜品基本信息(dish)
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //+ 拷贝一下
        BeanUtils.copyProperties(dish,dishDto);

        //2查询当前菜品口味信息(dish_flavor)
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);    //对应的口味数据查出来了
        dishDto.setFlavors(flavors);    //+因为要返回的是dishDto形式

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据(dish_flavor表的delete操作)
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据(dish_flavor表的Insert操作)
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {   //stream 遍历出来每个item都是一个实体类
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);



    }
}
