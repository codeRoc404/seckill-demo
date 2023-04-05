package com.zzp.seckilldemo.service;

import com.zzp.seckilldemo.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzp.seckilldemo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
public interface GoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoById(Long goodsId);
}
