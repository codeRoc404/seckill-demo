package com.zzp.seckilldemo.common.exception;

import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.Result;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author zzp
 * @Date 2023/3/23 12:40
 * @Description: 统一异常处理
 * @Version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = TokenApiException.class)
    public Result<?> handleTokenException(TokenApiException e) {
        log.error(e.getMessage(), e);
        return Result.error(CommonConstant.INVALID_TOKEN, e.getMessage());
    }

    @ExceptionHandler(value = BindException.class)
    public Result<?> handleValidException(BindException e) {
        return Result.error500("请注意输入格式");
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public Result<?> handleExpiredJwtException(ExpiredJwtException e) {
        return Result.error(CommonConstant.UNAUTHORIZED, "认证失效，请重新登录");
    }

    @ExceptionHandler(value = ShiroException.class)
    public Result<?> handleShiroException(ShiroException e) {
        return Result.error(CommonConstant.SC_NO_AUTHZ, "访问权限认证未通过");
    }
}
