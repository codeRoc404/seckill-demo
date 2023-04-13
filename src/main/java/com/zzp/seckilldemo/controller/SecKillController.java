package com.zzp.seckilldemo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.common.utils.MD5Util;
import com.zzp.seckilldemo.common.utils.distributedLock.SimpleRedisLock;
import com.zzp.seckilldemo.config.BucketRateLimiter;
import com.zzp.seckilldemo.config.rabbitmq.RabbitMQConsumer;
import com.zzp.seckilldemo.config.rabbitmq.RabbitMQSender;
import com.zzp.seckilldemo.entity.Goods;
import com.zzp.seckilldemo.entity.SeckillGoods;
import com.zzp.seckilldemo.entity.SeckillOrder;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.*;
import com.zzp.seckilldemo.vo.GoodsVo;
import com.zzp.seckilldemo.vo.SecKillVo;
import io.swagger.models.auth.In;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author zzp
 * @Date 2023/3/30 21:13
 * @Description: 商品秒杀
 * @Version 1.0
 */
@RestController
@RequestMapping("/secKill")
@Slf4j
public class SecKillController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private BucketRateLimiter bucketRateLimiter;


    //读取lua脚本文件,初始化
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        //设置脚本文件位置
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        //设置返回类型
        SECKILL_SCRIPT.setResultType(Long.class);
    }

//
//    /**
//     * 单服务器
//     * @param request       获取token
//     * @param goodsId       商品id
//     * @param purchaseCount 购买数量
//     */
//    @PostMapping("/doSecKill/{goodsId}/{purchaseCount}")
//    public Result doSecKill(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) {
//        //从token中获取userId
//        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));
//
//        /**
//         *   Long是一个对象,但相同值的Long每一次对象都是不同的，
//         *   所以要对值加锁,但是toString的底层是new String所以还是对象加锁
//         *   所以要调用String的intern方法，在常量池中找值相同的引用,则所有都一样
//         *   synchronized放在事务中（@Transactional）存在问题, Transaction的事务是在方法结束时才会提交,
//         *   在并发的情况下也会存在事务还没提交,锁已经释放,存在可能同一个ID进入同步块的情况，所以要放在@Transactional的外面
//         *    保证事务提交后，才能再次进入此方法
//         */
//        //锁住同一userId
//        synchronized (userId.toString().intern()) {
//            SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));
//
//            //判断库存
//            if (seckillGoods.getStockCount() < purchaseCount || seckillGoods.getStockCount() < 1) {
//                return Result.error(CommonConstant.SEC_KILL_601, "库存不足");
//            }
//            //判断用户是否重复抢购
//            long count = seckillOrderService.count(new QueryWrapper<SeckillOrder>()
//                    .eq("user_id", userId)
//                    .eq("goods_id", goodsId));
//            if (count != 0) {
//                return Result.error(CommonConstant.SEC_KILL_601, "不能重复抢购");
//            }
//            //扣减库存
//            //乐观锁（CAS方式）
//            boolean create = seckillGoodsService.update()
//                    .setSql("stock_count = stock_count - " + purchaseCount)
//                    .eq("goods_id", goodsId)
////                .eq("stock_count", seckillGoods.getStockCount()) //并发抢购失败率高，库存仍有余量
//                    .gt("stock_count", 0) //改进方案，库存大于0，即可扣减库存
//                    .update();
//            if (create) {
//                //创建订单 事务方法creatOrder
//                orderService.creatOrder(userId, goodsId, seckillGoods, purchaseCount);
//            }
//        }
//        return Result.ok("抢购成功");
//    }

    /**
     * 分布式/集群模式
     *
     * @param request       获取token
     * @param goodsId       商品id
     * @param purchaseCount 购买数量
     */
//    @PostMapping("/doSecKill/{goodsId}/{purchaseCount}")
//    public Result doSecKill(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) {
//
//        //从token中获取userId
//        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));
//
//        //创建锁对象,锁住同一userId
//        SimpleRedisLock redisLock = new SimpleRedisLock(redisTemplate, "order:" + userId);
//        //获取锁
//        boolean isLock = redisLock.tryLock(600);
//        //判断是否获取锁成功
//        if (!isLock) {
//            return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购");
//        }
//
//        try {
//            SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));
//            //判断库存
//            if (seckillGoods.getStockCount() < purchaseCount || seckillGoods.getStockCount() < 1) {
//                return Result.error(CommonConstant.SEC_KILL_601, "库存不足");
//            }
//
//            //判断用户是否重复抢购
//            long count = seckillOrderService.count(new QueryWrapper<SeckillOrder>()
//                    .eq("user_id", userId)
//                    .eq("goods_id", goodsId));
//            if (count != 0) {
//                return Result.error(CommonConstant.SEC_KILL_601, "不能重复抢购");
//            }
//            //扣减库存
//            //乐观锁（CAS方式）
//            boolean create = seckillGoodsService.update()
//                    .setSql("stock_count = stock_count - " + purchaseCount)
//                    .eq("goods_id", goodsId)
////                .eq("stock_count", seckillGoods.getStockCount()) //并发抢购失败率高，库存仍有余量
//                    .gt("stock_count", 0) //改进方案，库存大于0，即可扣减库存
//                    .update();
//            if (create) {
//                //创建订单 事务方法creatOrder
//                orderService.creatOrder(userId, goodsId, seckillGoods, purchaseCount);
//            }
//
//        } finally {
//            redisLock.unlock();
//        }
//
//        return Result.ok("抢购成功");
//    }

    /**
     * 基于redisson实现的分布式锁
     *
     * @param request
     * @param goodsId
     * @param purchaseCount
     * @return
     */
    @PostMapping("old/doSecKill/{goodsId}/{purchaseCount}")
    public Result doSecKillOld(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) throws InterruptedException {

        //从token中获取userId
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));

        //创建锁对象,锁住同一userId
//        SimpleRedisLock redisLock = new SimpleRedisLock(redisTemplate, "order:" + userId);
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        //获取锁
//        boolean isLock = redisLock.tryLock(600);
        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(1, 600, TimeUnit.SECONDS);
        //判断是否获取锁成功
        if (!isLock) {
            return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购");
        }

        try {
            SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));
            //判断库存
            if (seckillGoods.getStockCount() < purchaseCount || seckillGoods.getStockCount() < 1) {
                return Result.error(CommonConstant.SEC_KILL_601, "库存不足");
            }

            //判断用户是否重复抢购
            long count = seckillOrderService.count(new QueryWrapper<SeckillOrder>()
                    .eq("user_id", userId)
                    .eq("goods_id", goodsId));
            if (count != 0) {
                return Result.error(CommonConstant.SEC_KILL_601, "不能重复抢购");
            }
            //扣减库存
            //乐观锁（CAS方式）
            boolean create = seckillGoodsService.update()
                    .setSql("stock_count = stock_count - " + purchaseCount)
                    .eq("goods_id", goodsId)
//                .eq("stock_count", seckillGoods.getStockCount()) //并发抢购失败率高，库存仍有余量
                    .gt("stock_count", 0) //改进方案，库存大于0，即可扣减库存
                    .update();
            if (create) {
                //创建订单 事务方法creatOrder
                orderService.creatOrder(userId, goodsId, purchaseCount);
            }

        } finally {
            lock.unlock();
        }

        return Result.ok("抢购成功");
    }

    /**
     * 基于redisson实现的分布式锁
     * 优化版：
     * 将商品库存数量保存在redis中
     * 用户抢购后添加至redis的set集合中（set元素不重复），用于判断是否重复下单
     * sadd seckill:order:orderId userId (下单后加入key为 eckill:order:orderId 的set集合中)
     * sismember eckill:order:orderId userId 判断用户是否在该集合中
     *
     * @param request
     * @param goodsId
     * @param purchaseCount
     * @return
     */
    @PostMapping("/doSecKill/{path}/{goodsId}/{purchaseCount}")
    public Result doSecKill(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount, @PathVariable String path) throws JsonProcessingException, InterruptedException {

        //从token中获取userId
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));

        boolean check = orderService.checkPath(userId, goodsId, path);
        if (!check) {
            return Result.error(CommonConstant.SEC_KILL_601, "请求非法");
        }

        if (!bucketRateLimiter.acquireToken()) {
            throw new RuntimeException("请求频繁，请稍后重试");
        }
        //执行lua脚本
        Long r = (Long) redisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                goodsId,
                userId,
                purchaseCount);
        assert r != null;
        int result = r.intValue();

        //创建锁对象,锁住同一userId
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(1, 600, TimeUnit.SECONDS);
        //判断是否获取锁成功
        if (!isLock) {
            return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购1");
        }
        try {
            //判断库存
            if (result == 1) {
                return Result.error(CommonConstant.SEC_KILL_601, "库存不足");
            }
            //判断用户是否重复抢购
            if (result == 2) {
                return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购");
            }

            SecKillVo secKillVo = new SecKillVo(userId, goodsId, purchaseCount);
            String order = JSON.toJSONString(secKillVo);
            rabbitMQSender.sendSeckillOrder(order);
            return Result.ok("排队中");

        } finally {
            lock.unlock();
        }
    }

    @PostMapping("/doSecKill/{goodsId}/{purchaseCount}")
    public Result doSecKill2(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) throws JsonProcessingException, InterruptedException {

        //从token中获取userId
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));

        if (!bucketRateLimiter.acquireToken()) {
            throw new RuntimeException("请求频繁，请稍后重试");
        }
        //执行lua脚本
        Long r = (Long) redisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                goodsId,
                userId,
                purchaseCount);
        assert r != null;
        int result = r.intValue();

        //创建锁对象,锁住同一userId
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        //尝试获取锁，参数分别是：获取锁的最大等待时间(期间会重试)，锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(1, 600, TimeUnit.SECONDS);
        //判断是否获取锁成功
        if (!isLock) {
            return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购1");
        }
        try {
            //判断库存
            if (result == 1) {
                return Result.error(CommonConstant.SEC_KILL_601, "库存不足");
            }
            //判断用户是否重复抢购
            if (result == 2) {
                return Result.error(CommonConstant.SEC_KILL_601, "请勿重复抢购");
            }

            SecKillVo secKillVo = new SecKillVo(userId, goodsId, purchaseCount);
            String order = JSON.toJSONString(secKillVo);
            rabbitMQSender.sendSeckillOrder(order);
            return Result.ok("排队中");

        } finally {
            lock.unlock();
        }
    }

    /**
     * 随机生成字符串结合秒杀api路径，用于隐藏秒杀接口
     *
     * @param request
     * @param goodsId
     * @param purchaseCount
     * @return
     */
    @PostMapping("/path/{goodsId}/{purchaseCount}")
    public Result getPath(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) {
        //从token中获取userId
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));
        String path = orderService.createPath(userId, goodsId);
        return Result.ok(path);
    }

}
