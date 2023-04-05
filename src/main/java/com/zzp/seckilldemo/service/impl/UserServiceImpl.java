package com.zzp.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.exception.TokenApiException;
import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.entity.*;
import com.zzp.seckilldemo.mapper.*;
import com.zzp.seckilldemo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzp.seckilldemo.common.utils.MD5Util;
import com.zzp.seckilldemo.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author roc
 * @since 2023-03-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 登录判断
     *
     * @return
     */
    @Override
    public Result doLogin(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String password = loginVo.getPassword();

        User user = userMapper.selectById(phone);
        if (user == null) {
            throw new TokenApiException("用户不存在");
        }
        if (!MD5Util.pwdToDBPwd(password, user.getSalt()).equals(user.getPassword())) {
            throw new TokenApiException("手机号或密码错误");
        }

        //生成token
        String token = JwtUtil.creatToken(user.getId(), user.getNickname());
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.ok("登录成功", data);
    }

    @Override
    public List<String> getPerms(User user) {
        ArrayList<String> perms = new ArrayList<>();
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);

        for (UserRole userRole : userRoles) {
            QueryWrapper<RolePermission> rpWrapper = new QueryWrapper<>();
            rpWrapper.eq("role_id", userRole.getRoleId());
            List<RolePermission> rpList = rolePermissionMapper.selectList(rpWrapper);
            for (RolePermission rolePermission : rpList) {
                Permission permission = permissionMapper.selectById(rolePermission.getPermissionId());
                String permissionCode = permission.getPermissionCode();
                perms.add(permissionCode);
            }
        }
        return perms;
    }

    @Override
    public List<String> getRoles(User user) {
        ArrayList<String> roles = new ArrayList<>();
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);

        for (UserRole userRole : userRoles) {
            QueryWrapper<Role> rpWrapper = new QueryWrapper<>();
            rpWrapper.eq("id", userRole.getRoleId());
            List<Role> roleList = roleMapper.selectList(rpWrapper);
            for (Role role : roleList) {
                roles.add(role.getRoleName());
            }
        }
        return roles;
    }
}
