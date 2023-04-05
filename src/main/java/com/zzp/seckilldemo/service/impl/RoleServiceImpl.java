package com.zzp.seckilldemo.service.impl;

import com.zzp.seckilldemo.entity.Role;
import com.zzp.seckilldemo.mapper.RoleMapper;
import com.zzp.seckilldemo.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台角色表 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
