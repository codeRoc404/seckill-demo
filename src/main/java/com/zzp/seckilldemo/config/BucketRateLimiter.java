package com.zzp.seckilldemo.config;

import cn.hutool.core.lang.UUID;
import com.zzp.seckilldemo.service.SeckillGoodsService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


/**
 * @Author zzp
 * @Date 2023/4/12 19:54
 * @Description: 令牌桶限流
 * @Version 1.0
 */
@Component
@Slf4j
@EnableScheduling//需要使能@Scheduled注解，在类前面添加@EnableScheduling进行使能，否则定时注解默认无效
@Setter
public class BucketRateLimiter {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String BUCKET_KEY = "token_bucket";
    private static final int PERMITS = 300; // 每秒生成的令牌数
    private static final int CAPACITY = 3000; // 令牌桶容量

    /**
     * 定时向令牌桶中添加令牌
     * 每秒添加permits个token
     */
    @Scheduled(fixedDelay = 1000)
    public void addTokenToBucket() {
        try {
            long size = redisTemplate.opsForList().size(BUCKET_KEY);
            ArrayList<String> list = new ArrayList<>();
            if (size < CAPACITY) {
                for (int i = 0; i < PERMITS; i++) {
                    String uuid = UUID.randomUUID().toString(true) + "-";
                    list.add(uuid);
                }
                redisTemplate.opsForList().leftPushAll(BUCKET_KEY,list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证访问请求是否允许通过
     *
     * @return true：允许通过；false：限流中
     */
    public boolean acquireToken() {
        try {
            long size = redisTemplate.opsForList().size(BUCKET_KEY);
            if (size > 0) {
                redisTemplate.opsForList().rightPop(BUCKET_KEY);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
