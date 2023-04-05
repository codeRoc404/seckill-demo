package com.zzp.seckilldemo.common.utils.distributedLock;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Author zzp
 * @Date 2023/4/5 16:47
 * @Description: TODO
 * @Version 1.0
 */
public class SimpleRedisLock implements Lock {

    //读取lua脚本文件,初始化
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        //设置脚本文件位置
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        //设置返回类型
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    //锁名称前缀
    private static final String KEY_LOCK_PREFIX = "lock:";
    //value前缀 toString(true)去掉 uuid中的-
    private static final String VALUE_ID_PREFIX = UUID.randomUUID().toString(true) + "-";
    //锁名称
    private String lockName;

    private RedisTemplate redisTemplate;

    public SimpleRedisLock(RedisTemplate redisTemplate, String lockName) {
        this.lockName = lockName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        //获取线程标识
        String threadId = VALUE_ID_PREFIX + Thread.currentThread().getId();

        //获取锁 setnx
        Boolean success = redisTemplate.opsForValue().setIfAbsent(KEY_LOCK_PREFIX + lockName, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

//    @Override
//    public void unlock() {
//        //获取线程标识
//        String threadId = VALUE_ID_PREFIX + Thread.currentThread().getId();
//        //获取redis中存储的线程标识
//        String id = (String) redisTemplate.opsForValue().get(KEY_LOCK_PREFIX + lockName);
//
//        //判断是否一致
//        if (threadId.equals(id)) {
//            //释放锁
//            redisTemplate.delete(KEY_LOCK_PREFIX + lockName);
//        }
//    }

    @Override
    public void unlock() {
        //调用Lua脚本
        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_LOCK_PREFIX + lockName),
                VALUE_ID_PREFIX + Thread.currentThread().getId());
    }
}
