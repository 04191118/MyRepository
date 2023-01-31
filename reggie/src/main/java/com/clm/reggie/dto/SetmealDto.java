package com.clm.reggie.dto;

import com.clm.reggie.entity.Setmeal;
import com.clm.reggie.entity.SetmealDish;

import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
