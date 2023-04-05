package com.zzp.seckilldemo.controller;


import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.service.GoodsService;
import com.zzp.seckilldemo.service.UserService;
import com.zzp.seckilldemo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 获取商品列表
     *
     * @return
     */
    @GetMapping("/getGoodsList")
    public Result getGoodsList() {
        return Result.ok(goodsService.findGoodsVo());
    }

    /**
     * 商品详情
     *
     * @return
     */
    @PostMapping("/getGoodsDetail/{goodsId}")
    public Result getGoodsDetail(@PathVariable Long goodsId) {
        //秒杀状态 0:未开始 1：进行中 2：已结束
        int seckillStatus;
        GoodsVo goodsVo = goodsService.findGoodsVoById(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀未开始
        if (nowDate.before(startDate)) {
            seckillStatus = 0;
        } else if (nowDate.after(endDate)) {
            //秒杀已结束
            seckillStatus = 2;
        } else {
            seckillStatus = 1;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("seckillStatus", seckillStatus);
        map.put("goodsDetail", goodsVo);
        return Result.ok(map);
    }

}
