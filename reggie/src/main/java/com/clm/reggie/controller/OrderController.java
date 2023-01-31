package com.clm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.reggie.common.BaseContext;
import com.clm.reggie.common.R;
import com.clm.reggie.dto.OrdersDto;
import com.clm.reggie.entity.*;
import com.clm.reggie.service.OrderDetailService;
import com.clm.reggie.service.OrderService;
import com.clm.reggie.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交支付
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);

        return R.success("支付成功");
    }

    /**
     * 用户订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> getUserPage(@RequestParam int page,@RequestParam int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();


        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Orders::getUserId,BaseContext.GetCurrentId());

        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);

            Long orderId = item.getId();

            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();

            orderDetailQueryWrapper.eq(OrderDetail::getOrderId,orderId);

            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);

            ordersDto.setOrderDetails(orderDetails);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }

    /**
     * 后台订单详情
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam int page, @RequestParam int pageSize, String number, String beginTime, String endTime)
    {
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Orders::getOrderTime);

        queryWrapper.like(number != null, Orders::getNumber,number);
        queryWrapper.between((beginTime!=null && endTime != null),Orders::getOrderTime,beginTime,endTime);

        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        orderService.updateById(orders);
        return R.success("订单状态成功更改");
    }

    /**
     * 再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String,String> map){
        String ids = map.get("id");
        long id = Long.parseLong(ids);

        LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailQueryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);

        Long userId = BaseContext.GetCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);

        List<ShoppingCart> shoppingCartList = orderDetails.stream().map((item) ->{
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            if(item.getDishId() != null){
                shoppingCart.setDishId(item.getDishId());
            }else {
                shoppingCart.setSetmealId(item.getSetmealId());
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("再来一单提交成功");
    }
}
