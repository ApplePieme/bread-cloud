package com.breadme.breadcloud.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.dto.UserDto;
import com.breadme.breadcloud.entity.vo.UserVo;
import com.breadme.breadcloud.exception.BreadCloudException;
import com.breadme.breadcloud.mapper.UserMapper;
import com.breadme.breadcloud.service.UserService;
import com.breadme.breadcloud.util.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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

    @Value("${bread-cloud.oss.endpoint}")
    private String endpoint;

    @Value("${bread-cloud.oss.access-key-id}")
    private String accessKeyId;

    @Value("${bread-cloud.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${bread-cloud.oss.bucket-name}")
    private String bucketName;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void register(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(User.Fields.username, user.getUsername());
        if (Objects.nonNull(baseMapper.selectOne(wrapper))) {
            throw new BreadCloudException(Code.EXISTING_USER, "用户名已存在, 换一个试试吧");
        }
        wrapper.clear();
        wrapper.eq(User.Fields.phone, user.getPhone());
        if (Objects.nonNull(baseMapper.selectOne(wrapper))) {
            throw new BreadCloudException(Code.EXISTING_USER, "手机号已注册, 快去登录吧");
        }
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        user.setAvatar(defaultAvatar);
        user.setSalt(UUID.randomUUID().toString());
        user.setPassword(SecurityUtils.digest(SecurityUtils.decrypt(user.getPassword()) + user.getSalt()));
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
        String inputPassword = SecurityUtils.digest(SecurityUtils.decrypt(user.getPassword()) + dbUser.getSalt());
        if (!inputPassword.equals(dbUser.getPassword())) {
            throw new BreadCloudException(Code.FAIL, "密码错误");
        }
        String token = SecurityUtils.getToken(dbUser.getId().toString(), dbUser.getSalt());
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
    public void logout(String token) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constant.USER_TOKEN_KEY_PREFIX + token))) {
            throw new BreadCloudException(Code.FAIL, "你还没有登录哦");
        }
        redisTemplate.delete(Constant.USER_TOKEN_KEY_PREFIX + token);
        redisTemplate.delete(Constant.USER_INFO_KEY_PREFIX + "::" + token);
    }

    @Override
    @Cacheable(Constant.USER_INFO_KEY_PREFIX)
    public UserVo getUserInfo(String token) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(Constant.USER_TOKEN_KEY_PREFIX + token))) {
            throw new BreadCloudException(Code.FAIL, "你还没有登录哦");
        }
        Long id = Long.parseLong(SecurityUtils.getUserId(token));
        User user = baseMapper.selectById(id);
        if (Objects.isNull(user)) {
            log.error("数据库查询不到用户, JWT解析id={}", id);
            throw new BreadCloudException(Code.FAIL, "出了点小问题, 请重新登录");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        log.info("获取用户信息 => {}", user);
        return userVo;
    }

    @Override
    public void modifiedUserInfo(User user) {
        user.setId(UserContextUtils.getUserId());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(User.Fields.phone, user.getPhone());
        wrapper.ne(User.Fields.id, user.getId());
        if (Objects.nonNull(baseMapper.selectOne(wrapper))) {
            throw new BreadCloudException(Code.EXISTING_USER, "该手机号已被注册");
        }
        if (baseMapper.updateById(user) < 1) {
            throw new BreadCloudException(Code.FAIL, "修改信息失败, 请稍后再试");
        }
        redisTemplate.delete(Constant.USER_INFO_KEY_PREFIX + "::" + UserContextUtils.getToken());
    }

    @Override
    public void modifiedPassword(UserDto userDto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(User.Fields.id, UserContextUtils.getUserId());
        User dbUser = baseMapper.selectOne(wrapper);
        if (Objects.isNull(dbUser)) {
            throw new BreadCloudException(Code.FAIL, "出了点小问题, 请重新登录");
        }
        String srcPassword = SecurityUtils.decrypt(userDto.getSrcPassword());
        if (!dbUser.getPassword().equals(SecurityUtils.digest(srcPassword + dbUser.getSalt()))) {
            throw new BreadCloudException(Code.FAIL, "原密码错误, 请重试");
        }
        String newPassword = SecurityUtils.decrypt(userDto.getNewPassword());
        if (dbUser.getPassword().equals(SecurityUtils.digest(newPassword + dbUser.getSalt()))) {
            throw new BreadCloudException(Code.FAIL, "新密码不能和原密码一样");
        }
        dbUser.setSalt(UUID.randomUUID().toString());
        dbUser.setPassword(SecurityUtils.digest(newPassword + dbUser.getSalt()));
        if (baseMapper.updateById(dbUser) < 1) {
            throw new BreadCloudException(Code.FAIL, "修改密码失败, 请稍后再试");
        }
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        OSS oss = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest;
        String originalFilename = file.getOriginalFilename();
        if (file.isEmpty() || Objects.isNull(originalFilename)) {
            throw new BreadCloudException(Code.FAIL, "上传头像为空");
        }
        String extensionName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = "avatar/" + new DateTime().toString("yyyy/MM/dd");
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        String url = filePath + "/" + fileName + extensionName;
        String ret;
        try {
            putObjectRequest = new PutObjectRequest(bucketName, url, file.getInputStream());
            oss.putObject(putObjectRequest);
            ret = "https://" + bucketName + "." + endpoint + "/" + url;
        } catch (IOException e) {
            log.error("获取上传文件流失败\n", e);
            throw new BreadCloudException(Code.FAIL, "上传头像失败, 请稍后重试");
        } finally {
            oss.shutdown();
        }
        return ret;
    }
}
