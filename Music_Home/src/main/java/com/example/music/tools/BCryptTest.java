package com.example.music.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author qiu
 * @version 1.8.0
 */
public class BCryptTest {
    public static void main(String[] args) {

        String password = "123456";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //encode中的盐是随机加盐
        String newPassword = bCryptPasswordEncoder.encode(password);

        System.out.println("加密的密码为:" + newPassword);

        boolean sameRes = bCryptPasswordEncoder.matches(password,newPassword);
        System.out.println("加密的密码和原密码对比结果" + sameRes);

        boolean otherRes = bCryptPasswordEncoder.matches("123123",newPassword);
        System.out.println("检测match方法正确性(输出为false则为正确)" + otherRes);
    }
}
