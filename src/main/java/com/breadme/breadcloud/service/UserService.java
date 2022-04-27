package com.breadme.breadcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.vo.UserVo;

import java.util.Map;

/**
 * 用户 业务层
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:05
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param user 用户信息
     */
    void register(User user);

    /**
     * 用户登录
     *
     * @param user 登录信息
     * @return token 和 userVo
     */
    Map<String, Object> login(User user);

    /**
     * 获取用户信息
     *
     * @param token token
     * @return userVo
     */
    UserVo getUserInfo(String token);
}
