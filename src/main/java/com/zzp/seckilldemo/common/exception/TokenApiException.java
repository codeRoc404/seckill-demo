package com.zzp.seckilldemo.common.exception;

/**
 * @Author zzp
 * @Date 2023/3/23 13:02
 * @Description: 自定义异常
 * @Version 1.0
 */
public class TokenApiException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TokenApiException(String message) {
        super(message);
    }

    public TokenApiException(Throwable cause) {
        super(cause);
    }

    public TokenApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
