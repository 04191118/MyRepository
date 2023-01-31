package com.clm.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.reggie.entity.OrderDetail;
import com.clm.reggie.mapper.OrderDetailMapper;
import com.clm.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;


@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
