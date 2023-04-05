package com.zzp.seckilldemo;

import com.zzp.seckilldemo.common.utils.JwtUtil;
import com.zzp.seckilldemo.common.utils.MD5Util;
import com.zzp.seckilldemo.entity.User;
import com.zzp.seckilldemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class SeckillDemoApplicationTests {
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setId(15139769049L);
        List<String> perms = userService.getPerms(user);
        for (String s : perms) {
            System.out.println(s);
        }

    }

    @Test
    void test01() {
        System.out.println(MD5Util.inputPwdToDBPwd("123456", "asdasd"));
    }

    @Test
    void test02() {
        User user = new User();
        user.setId(18012099705L);
        List<String> roles = userService.getRoles(user);
        for (String s : roles) {
            System.out.println(s);
        }
    }


    /**
     * 生成用户token 用于JMeter测试
     */
    @Test
    public void generateToken() {
        List<User> userList = userService.list();

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\Goku\\Desktop\\tokens.csv"));) {
            for (User user : userList) {
                String token = JwtUtil.creatToken(user.getId(), user.getNickname()) + ",";
                bos.write(token.getBytes(StandardCharsets.UTF_8));
                bos.write("\r\n".getBytes()); //换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
