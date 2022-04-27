package com.breadme.breadcloud.util;

/**
 * 各种常量
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 22:07
 */
public interface Constant {
    /**
     * redis 用户 token key
     */
    String USER_TOKEN_KEY = "token::";

    /**
     * 手机号正则表达式
     */
    String PHONE_PATTERN = "/^1[3-9]\\d{9}$/";
}
