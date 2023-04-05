package com.zzp.seckilldemo.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author zzp
 * @Date 2023/3/27 22:05
 * @Description: TODO
 * @Version 1.0
 */
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}