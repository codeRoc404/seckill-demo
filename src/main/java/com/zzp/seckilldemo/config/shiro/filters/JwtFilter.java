package com.zzp.seckilldemo.config.shiro.filters;

import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.exception.TokenApiException;
import com.zzp.seckilldemo.config.shiro.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author zzp
 * @Date 2023/3/25 13:24
 * @Description: jwt过滤器
 * @Version 1.0
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {


    /**
     * 执行登录认证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            throw new TokenApiException("请重新登录");
        }
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN);
        JwtToken jwtToken = new JwtToken(token);
        /**
         *  提交给realm进行登入，如果错误他会抛出异常并被捕获
         *  这里getSubject方法实际上就是获得一个subject
         *  与原生shiro不同的地方在于没有对username和password进行封装
         *  直接使用jwt进行认真，login方法实际上就是交给Realm进行认证
         */
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

}
