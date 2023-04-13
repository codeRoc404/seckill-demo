package com.zzp.seckilldemo.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zzp
 * @Date 2023/4/11 16:16
 * @Description: RabbitMQ配置类
 * @Version 1.0
 */
@Configuration
public class RabbitMQConfig {

    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    //创建一个队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    //topic交换机
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }

    //绑定交换机与队列
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }

}
