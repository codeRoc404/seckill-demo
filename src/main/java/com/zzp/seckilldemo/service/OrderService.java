package com.zzp.seckilldemo.service;

import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.entity.Goods;
import com.zzp.seckilldemo.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzp.seckilldemo.entity.SeckillGoods;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.vo.GoodsVo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
public interface OrderService extends IService<Order> {

    Result creatOrder(Long userId, Long goodsId, Integer purchaseCount);

    /*
    生成秒杀地址
     */
    String createPath(Long userId, Long goodsId);

    /*
    检查path路径
     */
    boolean checkPath(Long userId, Long goodsId, String path);

}
