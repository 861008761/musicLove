package com.mylovin.music.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.util.ByteSource;

import java.util.UUID;

public class Md5SaltUtil {
    //摘要算法
    private static final String MD5_HASH = "md5";

    //加密次数
    private static final int HASH_ITERATIONS = 2;

    /**
     * 密码加密，采用shiro框架里的SimpleHash算法
     * 通过MD5算法+salt来加密，加密三次
     *
     * @param pass 原始密码
     * @param salt 盐值
     * @return 加密后的摘要
     */
    public static String encoderPassword(String pass, String salt) {
        Object object = new SimpleHash(MD5_HASH, pass, ByteSource.Util.bytes(salt), HASH_ITERATIONS);
        return object.toString();
    }

    //生成salt值，根据UUID和系统当前时间组合生成
    public static String generateSalt() {
        return DigestUtils.md5Hex(UUID.randomUUID().toString() + System.currentTimeMillis() + UUID.randomUUID().toString());
    }

    public static void main(String[] args) {
        System.out.println(encoderPassword("123456", "yc616474e09ed55b36543e46328ebbe709"));
    }
}
