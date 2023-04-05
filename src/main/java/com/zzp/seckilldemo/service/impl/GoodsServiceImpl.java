package com.zzp.seckilldemo.service.impl;

import com.zzp.seckilldemo.entity.Goods;
import com.zzp.seckilldemo.mapper.GoodsMapper;
import com.zzp.seckilldemo.service.GoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzp.seckilldemo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-29
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * 常用的SpringBoot缓存注解有@Cacheable、@CacheEvit、@CachePut，含义分别为：
     *
     * 1、@Cacheable：修饰方法或类，当我们访问它修饰的方法时，优先从缓存中获取，若缓存中存在，则直接获取缓存的值；缓存不存在时，执行方法，并将结果写入缓存，value表示缓存名，key表示自定义缓存key
     *
     * 2、@CacheEvit：删除缓存，一般用在更新或者删除方法上，value表示缓存名，allEntries表示是否清空所有缓存
     *
     * 3、@CachePut：不管缓存有没有，都将方法的返回结果写入缓存；适用于缓存更新
     * @return
     */
    @Cacheable(value = "goods",key = "'goodsList'")
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 通过商品id获取商品信息
     * @return
     */
    @Override
    public GoodsVo findGoodsVoById(Long goodsId) {
        return goodsMapper.findGoodsVoById(goodsId);
    }
}
