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
    private MD5Utils() {

    }

    public static String digest(String src) {
        return DigestUtils.md5DigestAsHex(src.getBytes(StandardCharsets.UTF_8));
    }
}
