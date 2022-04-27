package com.breadme.breadcloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.vo.UserVo;
import com.breadme.breadcloud.exception.BreadCloudException;
import com.breadme.breadcloud.mapper.UserMapper;
import com.breadme.breadcloud.service.UserService;
import com.breadme.breadcloud.util.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:10
 */
@Log4j2
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Value("${bread-cloud.default.avatar}")
    private String defaultAvatar;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void register(User user) {
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        user.setAvatar(defaultAvatar);
        user.setPassword(MD5Utils.digest(user.getPassword()));
        if (!save(user)) {
            throw new BreadCloudException(Code.FAIL, "注册失败, 再试一次吧");
        }
        log.info("用户注册成功 => {}", user);
    }

    @Override
    public Map<String, Object> login(User user) {
        user.setPassword(MD5Utils.digest(user.getPassword()));
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (ValidateUtils.checkPhone(user.getUsername())) {
            wrapper.eq(User.Fields.phone, user.getUsername());
        } else {
            wrapper.eq(User.Fields.username, user.getUsername());
        }
        wrapper.eq(User.Fields.password, user.getPassword());
        User cur = baseMapper.selectOne(wrapper);
        if (Objects.isNull(cur)) {
            throw new BreadCloudException(Code.FAIL, "用户名或密码错误");
        }
        String token = JwtUtils.token(cur.getId().toString(), cur.getNickname());
        redisTemplate.opsForValue().set(Constant.USER_TOKEN_KEY + token, "1", 7, TimeUnit.DAYS);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(cur, userVo);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userVo);
        resultMap.put("token", token);
        log.info("用户登录成功 => {}, token={}", cur, token);
        return resultMap;
    }

    @Override
    public UserVo getUserInfo(String token) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(Constant.USER_TOKEN_KEY + token))) {
            throw new BreadCloudException(Code.FAIL, "你还没有登录哦");
        }
        Long id = Long.parseLong(JwtUtils.id(token));
        User user = baseMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new BreadCloudException(Code.FAIL, "出了点小问题, 请重新登录");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        log.info("获取用户信息 => {}", user);
        return userVo;
    }
}
