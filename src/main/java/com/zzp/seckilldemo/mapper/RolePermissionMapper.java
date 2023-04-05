package com.zzp.seckilldemo.mapper;

import com.zzp.seckilldemo.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色-权限关联表 Mapper 接口
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
