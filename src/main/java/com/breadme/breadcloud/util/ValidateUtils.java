package com.breadme.breadcloud.util;

/**
 * 校验工具类
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 1:47
 */
public class ValidateUtils {
    private ValidateUtils() {

    }

    public static boolean checkPhone(String phone) {
        return phone.matches(Constant.PHONE_PATTERN);
    }
}
