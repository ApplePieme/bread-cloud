package com.breadme.breadcloud.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * MD5加密
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 1:05
 */
public class MD5Utils {
    public static final String FIRST_KEY = "Ns1lkw234uL6KI8aua8skL4KJGFui92d";
    public static final String SECOND_KEY = "lk8hKhuKHEgsWjh89KLJqsPd63huw41O";

    private MD5Utils() {

    }

    public static String digest(String src) {
        String ret = src + FIRST_KEY;
        ret = DigestUtils.md5DigestAsHex(ret.getBytes(StandardCharsets.UTF_8)) + SECOND_KEY;
        return DigestUtils.md5DigestAsHex(ret.getBytes(StandardCharsets.UTF_8));
    }
}
