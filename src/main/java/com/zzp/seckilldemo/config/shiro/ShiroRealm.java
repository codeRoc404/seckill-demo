package com.zzp.seckilldemo.config.shiro;

import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author zzp
 * @Date 2023/3/27 19:52
 * @Description: 自定义realm
 * @Version 1.0
 */
@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 必须重写此方法，不然Shiro会报错
     * 限定这个realm只能处理JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("===============Shiro权限认证开始============ [ roles、permissions]==========");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (principals != null){
            // 获取认证方法中查到的当前登录用户信息 : User 对象
            User currentUser = (User) principals.getPrimaryPrincipal();
            // 获取当前用户在数据库中查询到的拥有的权限, 并为当前用户设置该权限
            info.addStringPermissions(userService.getPerms(currentUser));
        }
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("===============Shiro身份认证开始============doGetAuthenticationInfo==========");

        //获取token信息
        String token = (String) auth.getCredentials();
        // 校验token：未校验通过或者已过期
        if (!JwtUtil.verifyToken(token)) {
            throw new AuthenticationException("请先登录");
        }
        Long userId = JwtUtil.getUserIdByJwtToken(token);
        User user = userService.getById(userId);
        if (user == null) {
            throw new AuthenticationException("用户不存在!");
        }
        return new SimpleAuthenticationInfo(user, token, getName());
    }

    /**
     * 清除当前用户的权限认证缓存
     *
     * @param principals 权限信息
     */
    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
