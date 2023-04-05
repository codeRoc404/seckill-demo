package com.zzp.seckilldemo.common;

/**
 * @Author zzp
 * @Date 2023/3/22 14:16
 * @Description: 通用常量
 * @Version 1.0
 */
public interface CommonConstant {

    /**
     * 请求成功 200
     */
    Integer SC_OK_200 = 200;

    /**
     * 错误请求 400
     */
    Integer BAD_REQUEST = 400;

    /**
     * 未授权 401
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 服务器内部错误 500
     */
    Integer SC_INTERNAL_SERVER_ERROR_500 = 500;

    /**
     * token无效 501
     */
    Integer INVALID_TOKEN = 501;

    /**
     * 访问权限认证未通过 510
     */
    Integer SC_NO_AUTHZ = 510;

    /**
     * token密钥
     */
    String TOKEN_SECRET = "coderoc666888";

    /**
     * token有效时长
     */
    Long TOKEN_EXPIRE = 1000 * 60 * 60L;

    /**
     * token名称
     */
    String X_ACCESS_TOKEN = "token";

    /**
     * 秒杀异常 601
     */
    Integer SEC_KILL_601= 601;

}
