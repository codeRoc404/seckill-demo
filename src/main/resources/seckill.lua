-- 商品id
local goodsId = ARGV[1]
-- 用户id
local userId = ARGV[2]
-- 购买数量
local purchaseCount = ARGV[3]

-- 库存key
local stockKey = 'seckill:stock:' .. goodsId
-- 订单key
local orderKey = 'seckill:order:' .. goodsId

-- 判断库存是否充足
if (tonumber(redis.call('get', stockKey)) <= 0) then
    -- 库存不足
    return 1
end
-- 判断用户是否下单 sismember orderKey userId
if (redis.call('sismember', orderKey, userId) == 1) then
    -- 存在说明是重复下单
    return 2
end
-- 扣减库存 incrby stockKey -购买数量
redis.call('incrby', stockKey, -purchaseCount)
-- 下单（保存用户）sadd orderKey userId
redis.call('sadd', orderKey, userId)
return 0
