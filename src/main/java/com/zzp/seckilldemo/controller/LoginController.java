package com.zzp.seckilldemo.controller;

import com.zzp.seckilldemo.common.Result;
import com.zzp.seckilldemo.service.UserService;
import com.zzp.seckilldemo.vo.LoginVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author zzp
 * @Date 2023/3/20 17:32
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public Result login(@Valid @RequestBody LoginVo loginVo){
        return userService.doLogin(loginVo);
    }

    /**
     * 用户登录
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出接口")
    public Result logout(){
        return Result.ok("退出登录成功");
    }
}
