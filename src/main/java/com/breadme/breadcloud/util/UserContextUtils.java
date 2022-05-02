package com.breadme.breadcloud.util;

import org.slf4j.MDC;

/**
 * 用户工具类
 *
 * @author breadme@foxmail.com
 * @date 2022/5/2 13:55
 */
public class UserContextUtils {
    private UserContextUtils() {

    }

    /**
     * 获取当前登录的用户token
     *
     * @return token
     */
    public static String getToken() {
        return MDC.get("token");
    }

    /**
     * 获取当前登录的用户id
     *
     * @return 用户id
     */
    public static Long getUserId() {
        return Long.parseLong(MDC.get("userId"));
    }
}
