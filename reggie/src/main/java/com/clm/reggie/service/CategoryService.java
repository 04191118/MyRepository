package com.clm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.reggie.entity.Category;



public interface CategoryService extends IService<Category> {
    //删除分类信息
    public void remove(Long id);
}
