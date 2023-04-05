package com.zzp.seckilldemo.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author zzp
 * @Date 2023/3/19 18:44
 * @Description: md5
 * @Version 1.0
 */
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    //固定salt
    public static String inputPwdToPwd(String inputPwd) {
        //连续两个char类型相加会变成数字相加,使用""防止转变
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPwd + salt.charAt(5) + salt.charAt(4);
        System.out.println(str);
        return md5(str);
    }

    //随机salt
    public static String pwdToDBPwd(String pwd, String salt) {
        //连续两个char类型相加会变成数字相加
        String str = "" + salt.charAt(0) + salt.charAt(2) + pwd + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPwdToDBPwd(String inputPwd, String salt) {
        String pwd = inputPwdToPwd(inputPwd);
        return pwdToDBPwd(pwd, salt);
    }
}
