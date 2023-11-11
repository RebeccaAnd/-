package com.heima.reggie.dto;

import com.heima.reggie.config.entity.Dish;
import com.heima.reggie.config.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
