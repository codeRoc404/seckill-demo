package com.zzp.seckilldemo.config.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.zzp.seckilldemo.service.OrderService;
import com.zzp.seckilldemo.service.SeckillGoodsService;
import com.zzp.seckilldemo.vo.SecKillVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author zzp
 * @Date 2023/4/11 18:17
 * @Description: 消息消费方
 * @Version 1.0
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @RabbitListener(queues = "seckillQueue")
    public void consumeSeckillOrder(String msg) {
        SecKillVo secKillVo = JSON.parseObject(msg, SecKillVo.class);
        Long userId = secKillVo.getUserId();
        Long goodsId = secKillVo.getGoodsId();
        int purchaseCount = secKillVo.getPurchaseCount();
        //扣减库存
        //乐观锁（CAS方式）
        boolean create = seckillGoodsService.update()
                .setSql("stock_count = stock_count - " + purchaseCount)
                .eq("goods_id", goodsId)
                .gt("stock_count", 0) //改进方案，库存大于0，即可扣减库存
                .update();
        if (create) {
            //创建订单
            orderService.creatOrder(userId, goodsId, purchaseCount);
        }
    }
}
