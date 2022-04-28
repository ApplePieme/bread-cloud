package com.breadme.breadcloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 修改redis的序列化配置
 * 避免写入redis的key中有奇怪的字符（并不影响使用，只是我有强迫症）
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 19:09
 */
@Configuration
public class RedisConfig {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
    }
}
