package com.zzp.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.entity.Order;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/info")
    public Result getOrder(HttpServletRequest request){
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader("token"));
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Order> orderList = orderService.list(queryWrapper);
        return Result.ok(orderList);
    }

}
