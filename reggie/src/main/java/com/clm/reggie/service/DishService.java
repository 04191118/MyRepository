package com.clm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.reggie.dto.DishDto;
import com.clm.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品信息及其口味信息
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息及其口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息及其口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品信息及其口味信息
    public void deleteWithFlavor(List<Long> id);
}
