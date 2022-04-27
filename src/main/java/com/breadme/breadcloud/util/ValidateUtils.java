package com.breadme.breadcloud.util;

import java.util.regex.Pattern;

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
        return Pattern.matches(Constant.PHONE_PATTERN, phone);
    }
}
