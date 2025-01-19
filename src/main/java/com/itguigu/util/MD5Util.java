package com.itguigu.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2023/4/25 21:06
 **/
public class MD5Util {
    public static String encryptWith32Bit(String text) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5 = digest.digest(bytes);
            //转换为16进制字符
            for (byte b : md5) {
                sb.append(String.format("%02x",b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String encryptWith16Bit(String text) {
        //取32位数的16进制字符串中的 8-24之间的位数
        return encryptWith32Bit(text).substring(8, 24);
    }

}
