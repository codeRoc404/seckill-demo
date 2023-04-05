package com.zzp.seckilldemo.common.utils;

/**
 * @Author zzp
 * @Date 2023/3/24 23:23
 * @Description: JWT工具类
 * @Version 1.0
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzp.seckilldemo.common.CommonConstant;
import com.zzp.seckilldemo.common.exception.TokenApiException;
import io.jsonwebtoken.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtil {

    //密钥
    private static final String secret = CommonConstant.TOKEN_SECRET;
    //有效时长
    private static final long expire = CommonConstant.TOKEN_EXPIRE;

    /**
     * 创建token
     *
     * @param id
     * @param nickname
     * @return
     */
    public static String creatToken(Long id, String nickname) {
        Date nowDate = new Date();
        Date expiredDate = new Date(nowDate.getTime() + expire);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(nowDate)
                .setExpiration(expiredDate)
                .claim("id", id) //自定义声明
                .claim("username", nickname)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 验证 token 是否存在且有效
     *
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
            if (StringUtils.isEmpty(jwtToken))
                return false;
            Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
        } catch (ExpiredJwtException e) {
            throw new TokenApiException("验证已过期，请重新登录");
        } catch (Exception e) {
            throw new TokenApiException("token验证失败");
        }
        return true;
    }

    public static boolean verifyToken(String token) {
        try {
            if (StringUtils.isEmpty(token))
                return false;
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenApiException("验证已过期，请重新登录");
        } catch (Exception e) {
            throw new TokenApiException("token验证失败");
        }
        return true;
    }

    /**
     * 根据token获取用户id
     *
     * @param token
     * @return
     */
    public static Long getUserIdByJwtToken(String token) {
        if (StringUtils.isEmpty(token))
            throw new TokenApiException("请先登录");
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (Long)claims.get("id");
    }
}
