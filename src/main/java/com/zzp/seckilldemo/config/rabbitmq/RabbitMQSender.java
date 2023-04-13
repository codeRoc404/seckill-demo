package com.zzp.seckilldemo.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author zzp
 * @Date 2023/4/11 17:39
 * @Description: 消息发送方
 * @Version 1.0
 */
@Slf4j
@Component
public class RabbitMQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillOrder(String msg) {
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.msg", msg);
    }
}
