package com.zzp.seckilldemo.controller;

import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @Author zzp
 * @Date 2023/3/23 22:28
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    @RequiresPermissions("user:list")
    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        Long id = JwtUtil.getUserIdByJwtToken(request.getHeader("token"));
        User user = userService.getById(id);
        HashMap<String, String> info = new HashMap<>();
        info.put("name", user.getNickname());
        return Result.ok(info);
    }

    @GetMapping("/unauth")
    public Result unauth() {
        return Result.error(CommonConstant.UNAUTHORIZED, "未授权");
    }
}
