package com.clm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.reggie.dto.SetmealDto;
import com.clm.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    //根据id查询套餐信息及其关联菜品信息
    public SetmealDto getByIdWithDish(Long id);

    //新增套餐信息，同时保存套餐和菜品的关联信息
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐信息，同时删除套餐和菜品的关联信息
    public void removeWithDish(List<Long> ids);

    //更新套餐信息，同时更新套餐和菜品的关联信息
    public void updateWithDish(SetmealDto setmealDto);
}
