package com.zzp.seckilldemo.service;

import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzp.seckilldemo.vo.LoginVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author roc
 * @since 2023-03-19
 */
public interface UserService extends IService<User> {

    /**
     * 登录判断
     * @return
     */
    Result doLogin(LoginVo loginVo);

    List<String> getPerms(User user);

    List<String> getRoles(User user);

}
