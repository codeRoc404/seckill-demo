package com.zzp.seckilldemo.common.utils.distributedLock;

/**
 * @Author zzp
 * @Date 2023/4/5 16:45
 * @Description: TODO
 * @Version 1.0
 */
public interface Lock {

    /**
     * 尝试获取锁
     * @param timeoutSec 锁持有时间，过期后自动释放
     * @return true：获取成功 false：获取失败
     */
    boolean tryLock(long timeoutSec);

    //释放锁
    void unlock();
}
