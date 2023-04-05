package com.zzp.seckilldemo.mapper;

import com.zzp.seckilldemo.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 后台权限表 Mapper 接口
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
