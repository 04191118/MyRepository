package com.clm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);
}
