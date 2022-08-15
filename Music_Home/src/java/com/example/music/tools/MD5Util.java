package com.example.music.tools;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author qiu
 * @version 1.8.0
 */
public class MD5Util {
    private static final String salt = "hqellrox";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密:客户端加盐,加密
     * @param input
     * @return
     */
    public static String inputPassToFormPass(String input){
        String str = "" + salt.charAt(1) + input + salt.charAt(5) +salt.charAt(7);
        return md5(str);
    }

    /**
     * 第二次加密:服务器加盐加密
     * @param input
     * @param salt
     * @return
     */
    public static String inputFormPassToDBPass(String input,String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + input + salt.charAt(3)+salt.charAt(4)+salt.charAt(6);
        return md5(str);
    }

    public static String inputPassToDBPass(String input,String salt){
        String formPass = inputPassToFormPass(input);
        //前后端盐值可以不相同,盐值最后是随机的.
        return inputFormPassToDBPass(formPass,salt);
    }
}
