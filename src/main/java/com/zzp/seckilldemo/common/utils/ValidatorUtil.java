package com.zzp.seckilldemo.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author zzp
 * @Date 2023/3/22 19:18
 * @Description: 手机号格式校验规则
 * @Version 1.0
 */
public class ValidatorUtil {

    private static final Pattern phone_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");

    public static boolean isPhone(String phone){
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        Matcher matcher = phone_pattern.matcher(phone);
        return matcher.matches();
    }
}
