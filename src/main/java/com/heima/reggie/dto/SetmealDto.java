package com.heima.reggie.dto;

import com.heima.reggie.config.entity.Setmeal;
import com.heima.reggie.config.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
