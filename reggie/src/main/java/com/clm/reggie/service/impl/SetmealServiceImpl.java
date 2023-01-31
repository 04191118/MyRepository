package com.clm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.reggie.common.CustomException;
import com.clm.reggie.dto.SetmealDto;
import com.clm.reggie.entity.DishFlavor;
import com.clm.reggie.entity.Setmeal;
import com.clm.reggie.entity.SetmealDish;
import com.clm.reggie.mapper.SetmealMapper;
import com.clm.reggie.service.SetmealDishService;
import com.clm.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 根据id查询套餐信息及其关联菜品信息
     * @param id
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐信息，同时删除套餐和菜品的关联信息
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();

        setmealQueryWrapper.in(Setmeal::getId,ids);

        setmealQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(setmealQueryWrapper);

        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();

        setmealDishQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishQueryWrapper);
    }

    /**
     * 更新套餐信息，同时更新套餐和菜品的关联信息
     * @param setmealDto
     */
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();

        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);

    }
}
