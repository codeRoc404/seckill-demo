package com.zzp.seckilldemo.service.impl;

import com.zzp.seckilldemo.entity.RolePermission;
import com.zzp.seckilldemo.mapper.RolePermissionMapper;
import com.zzp.seckilldemo.service.RolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色-权限关联表 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
