package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.config.entity.Category;
import com.heima.reggie.config.entity.Dish;
import com.heima.reggie.config.entity.DishFlavor;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.DishFlavorService;
import com.heima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /*
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {   //声明一个参数来接收提交过来的JSON数据--但是参数不都在dish中,引入DTO
//        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /*
     * 菜品信息分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造 分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //+
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            //添加过滤条件
        queryWrapper.like(name!= null,Dish::getName,name);
            //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);
        //--但是由于Dish里面的属性不全,不能直接return,需要新建+
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");   //records需要手动处理

        List<Dish> records = pageInfo.getRecords(); //处理一下

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();    //1处理item对象的过程就是创建新的dto-通过对象拷贝基本数据
            //2 通过item得到分类Id,最终目的是查出分类名称

            BeanUtils.copyProperties(item,dishDto); //对象拷贝->普通属性都拷贝上来了

            Long categoryId = item.getCategoryId(); //得到分类ID
            Category category = categoryService.getById(categoryId);    //得到分类对象
            String categoryName = category.getName();   //最终目的,得到名称,单独插入
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /*
     * 新增菜品-根据Id查询菜品信息 和对应的口味信息
     * 其中Dto会把这两方面的数据都封装上
     */

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {  //id在请求URL里面,来接收参数

        DishDto dishDto = dishService.getByIdWithFlavor(id);    //主要再Service中编写了

        return R.success(dishDto);
    }

    /*
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {   //声明一个参数来接收提交过来的JSON数据--但是参数不都在dish中,引入DTO
        log.info(dishDto.toString());   //设计两张表操作

        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /*
     * 起售停售/删除菜品
     */
    //停售起售菜品
    @PostMapping("/status/{status}")    //指定菜品的状态
    public R<String> sale(@PathVariable int status,String[] ids) {
        for (String id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(String[] ids) {
        for(String id:ids) {
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

    /**
     * 根据条件查询对应菜品数据(后续flavors原因进行修改)
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        //构造查询条件对象
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1); //添加条件,状态为1(起售状态的菜品)
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);//最后拿到list集合
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> get(Dish dish) {
        //条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据传进来的categoryId查询
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId, dish.getCategoryId());
        //只查询状态为1的菜品（在售菜品）
        queryWrapper.eq(Dish::getStatus, 1);
        //简单排下序，其实也没啥太大作用
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //获取查询到的结果作为返回值
        List<Dish> list = dishService.list(queryWrapper);
        log.info("查询到的菜品信息list:{}",list);
        //遍历list中的每一条数据
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            //创建一个dishDto对象
            DishDto dishDto = new DishDto();
            //将item的属性全都copy到dishDto里
            BeanUtils.copyProperties(item, dishDto);
            //由于dish表中没有categoryName属性，只存了categoryId
            Long categoryId = item.getCategoryId();
            //所以我们要根据categoryId查询对应的category
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //然后取出categoryName，赋值给dishDto
                dishDto.setCategoryName(category.getName());
            }
            //然后获取一下菜品id，根据菜品id去dishFlavor表中查询对应的口味，并赋值给dishDto
            Long itemId = item.getId();
            //条件构造器
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //条件就是菜品id
            lambdaQueryWrapper.eq(itemId != null, DishFlavor::getDishId, itemId);
            //根据菜品id，查询到菜品口味
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            //赋给dishDto的对应属性
            dishDto.setFlavors(flavors);
            //并将dishDto作为结果返回
            return dishDto;
            //将所有返回结果收集起来，封装成List
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }


}
