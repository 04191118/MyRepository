package com.clm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.reggie.common.CustomException;
import com.clm.reggie.dto.DishDto;
import com.clm.reggie.entity.Dish;
import com.clm.reggie.entity.DishFlavor;
import com.clm.reggie.entity.Setmeal;
import com.clm.reggie.entity.SetmealDish;
import com.clm.reggie.mapper.DishMapper;
import com.clm.reggie.service.DishFlavorService;
import com.clm.reggie.service.DishService;
import com.clm.reggie.service.SetmealDishService;
import com.clm.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * //新增菜品信息及其口味信息
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * //根据id查询菜品信息及其口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 更新菜品信息及其口味信息
     * @param dishDto
     */
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 删除菜品信息及其口味信息
     * @param ids
     */
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();

        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();

        LambdaQueryWrapper<SetmealDish> setmealQueryWrapper = new LambdaQueryWrapper<>();

        dishQueryWrapper.in(Dish::getId, ids);

        List<Dish> dishes = dishService.list(dishQueryWrapper);

        for (Dish dish : dishes) {
            if(dish.getStatus() == 1){
                throw new CustomException("该菜品为起售状态，不能删除");
            }

            setmealQueryWrapper.eq(SetmealDish::getDishId,dish.getId());
            int count = setmealDishService.count(setmealQueryWrapper);

            if (count > 0 ){
                throw new CustomException("当前菜品关联了套餐，不能删除");
            }

            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
            dishFlavorService.remove(dishFlavorQueryWrapper);
        }

        this.removeByIds(ids);
    }

}
