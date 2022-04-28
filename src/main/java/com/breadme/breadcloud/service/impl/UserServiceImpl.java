package com.breadme.breadcloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.vo.UserVo;
import com.breadme.breadcloud.exception.BreadCloudException;
import com.breadme.breadcloud.mapper.UserMapper;
import com.breadme.breadcloud.service.UserService;
import com.breadme.breadcloud.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:10
 */
@Slf4j
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
        user.setSalt(UUID.randomUUID().toString());
        user.setPassword(MD5Utils.digest(user.getPassword() + user.getSalt()));
        if (!save(user)) {
            throw new BreadCloudException(Code.FAIL, "注册失败, 再试一次吧");
        }
        log.info("用户注册成功 => {}", user);
    }

    @Override
    public Map<String, Object> login(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (ValidateUtils.checkPhone(user.getUsername())) {
            wrapper.eq(User.Fields.phone, user.getUsername());
        } else {
            wrapper.eq(User.Fields.username, user.getUsername());
        }
        User dbUser = baseMapper.selectOne(wrapper);
        if (Objects.isNull(dbUser)) {
            throw new BreadCloudException(Code.FAIL, "用户名或手机号未注册");
        }
        String inputPassword = MD5Utils.digest(user.getPassword() + dbUser.getSalt());
        if (!inputPassword.equals(dbUser.getPassword())) {
            throw new BreadCloudException(Code.FAIL, "密码错误");
        }
        String token = JwtUtils.token(dbUser.getId().toString(), dbUser.getSalt());
        redisTemplate.opsForValue().set(Constant.USER_TOKEN_KEY_PREFIX + token, "1", 24, TimeUnit.HOURS);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(dbUser, userVo);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", userVo);
        resultMap.put("token", token);
        log.info("用户登录成功 => {}, token={}", dbUser, token);
        return resultMap;
    }

    @Override
    @Cacheable(Constant.USER_INFO_KEY_PREFIX)
    public UserVo getUserInfo(String token) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constant.USER_TOKEN_KEY_PREFIX + token))) {
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
