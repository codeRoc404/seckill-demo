package com.zzp.seckilldemo.service.impl;

import com.zzp.seckilldemo.entity.UserRole;
import com.zzp.seckilldemo.mapper.UserRoleMapper;
import com.zzp.seckilldemo.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
