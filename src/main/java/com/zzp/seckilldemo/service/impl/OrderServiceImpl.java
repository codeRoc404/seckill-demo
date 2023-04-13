package com.zzp.seckilldemo.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.utils.MD5Util;
import com.zzp.seckilldemo.entity.*;
import com.zzp.seckilldemo.mapper.GoodsMapper;
import com.zzp.seckilldemo.mapper.OrderMapper;
import com.zzp.seckilldemo.mapper.SeckillGoodsMapper;
import com.zzp.seckilldemo.mapper.SeckillOrderMapper;
import com.zzp.seckilldemo.service.GoodsService;
import com.zzp.seckilldemo.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzp.seckilldemo.service.SeckillGoodsService;
import com.zzp.seckilldemo.service.SeckillOrderService;
import com.zzp.seckilldemo.vo.GoodsVo;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建订单 t_order表
     *
     * @param userId        用户id
     * @param goodsId       商品id
     * @param purchaseCount 抢购数量
     */
    @Override
    @Transactional
    public Result<Object> creatOrder(Long userId, Long goodsId, Integer purchaseCount) {

        Goods goods;
        if (redisTemplate.opsForValue().get("goods:" + goodsId) != null) {
            goods = (Goods) redisTemplate.opsForValue().get("goods:" + goodsId);
        } else {
            goods = goodsService.getById(goodsId);
            //缓存商品信息
            redisTemplate.opsForValue().set("goods:" + goodsId, goods, 300, TimeUnit.SECONDS);
        }
        if (goods == null) {
            return Result.error(CommonConstant.SEC_KILL_601, "订单创建失败");
        }

        SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));

        //生成订单
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(purchaseCount);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);//1：pc端
        order.setStatus(0);//0：创建未支付
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(userId);
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setOrderId(order.getId());
        seckillOrderMapper.insert(seckillOrder);

        //秒杀成功，商品总库存减少
//        Goods goodsTotal = goodsMapper.selectById(goods.getId());
//        goodsTotal.setGoodsStock(goodsTotal.getGoodsStock() - purchaseCount);
//        goodsMapper.updateById(goodsTotal);
        return Result.ok("抢购成功");
    }

    /**
     * 秒杀接口隐藏
     * 生成秒杀地址
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(Long userId, Long goodsId) {
        String path = MD5Util.md5(UUID.randomUUID().toString(true) + "-");
        redisTemplate.opsForValue().set("seckill:path:" + userId + goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    /**
     * 秒杀接口隐藏
     * 检查生成的秒杀接口路径
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(Long userId, Long goodsId, String path) {
        String oPath = (String) redisTemplate.opsForValue().get("seckill:path:" + userId + goodsId);
        if (StringUtils.isEmpty(oPath))
            return false;
        return oPath.equals(path);
    }
}
