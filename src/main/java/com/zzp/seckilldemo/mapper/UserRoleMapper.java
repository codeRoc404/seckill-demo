package com.zzp.seckilldemo.mapper;

import com.zzp.seckilldemo.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户-角色关联表 Mapper 接口
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
