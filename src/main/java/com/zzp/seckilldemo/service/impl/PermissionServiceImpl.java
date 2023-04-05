package com.zzp.seckilldemo.service.impl;

import com.zzp.seckilldemo.entity.Permission;
import com.zzp.seckilldemo.mapper.PermissionMapper;
import com.zzp.seckilldemo.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台权限表 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-28
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
