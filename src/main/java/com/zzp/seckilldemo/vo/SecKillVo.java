package com.zzp.seckilldemo.vo;

import com.zzp.seckilldemo.entity.SeckillGoods;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zzp
 * @Date 2023/4/11 17:49
 * @Description: TODO
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillVo {
    private Long userId;
    private Long goodsId;
    private Integer purchaseCount;
}
