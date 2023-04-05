package com.zzp.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.common.utils.distributedLock.SimpleRedisLock;
import com.zzp.seckilldemo.entity.Goods;
import com.zzp.seckilldemo.entity.SeckillGoods;
import com.zzp.seckilldemo.entity.SeckillOrder;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.*;
import com.zzp.seckilldemo.vo.GoodsVo;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @Author zzp
 * @Date 2023/3/30 21:13
 * @Description: 商品秒杀
 * @Version 1.0
 */
@RestController
@RequestMapping("/secKill")
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
    @PostMapping("/doSecKill/{goodsId}/{purchaseCount}")
    public Result doSecKill(HttpServletRequest request, @PathVariable("goodsId") Long goodsId, @PathVariable("purchaseCount") Integer purchaseCount) {

        //从token中获取userId
        Long userId = JwtUtil.getUserIdByJwtToken(request.getHeader(CommonConstant.X_ACCESS_TOKEN));

        //创建锁对象,锁住同一userId
        SimpleRedisLock redisLock = new SimpleRedisLock(redisTemplate, "order:" + userId);
        //获取锁
        boolean isLock = redisLock.tryLock(600);
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
                orderService.creatOrder(userId, goodsId, seckillGoods, purchaseCount);
            }

        } finally {
            redisLock.unlock();
        }

        return Result.ok("抢购成功");
    }

}
