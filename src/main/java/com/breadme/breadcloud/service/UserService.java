package com.breadme.breadcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.dto.UserDto;
import com.breadme.breadcloud.entity.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

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
     * 退出
     *
     * @param token token
     */
    void logout(String token);

    /**
     * 获取用户信息
     *
     * @param token token
     * @return userVo
     */
    UserVo getUserInfo(String token);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     */
    void modifiedUserInfo(User user);

    /**
     * 修改密码
     */
    void modifiedPassword(UserDto userDto);

    /**
     * 上传头像
     *
     * @param file 头像
     * @return 头像url
     */
    String uploadAvatar(MultipartFile file);
}
