package com.zzp.seckilldemo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zzp
 * @Date 2023/4/10 17:06
 * @Description: redisson配置类
 * @Version 1.0
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {

        //配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.15.129:6379").setPassword("zzp");

        return Redisson.create(config);
    }
}
