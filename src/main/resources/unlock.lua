-- 锁的key
-- local key = KEYS[1]
-- 当前线程标识
-- local threadId = ARGV[1]

-- 获取锁中的线程标识
-- local id = redis.call('get', KEYS[1])

-- 比较线程标识与锁中的标识是否一致
if (redis.call('get', KEYS[1]) == ARGV[1]) then
    -- 释放锁
    return redis.call('del', KEYS[1])
end
-- 不一致，则直接返回
return 0