package com.blog.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;
//存入（带有过期时间）
    public  void set(String key,String value,long timeout){
        redisTemplate.opsForValue().set(key,value,timeout, TimeUnit.SECONDS);
    }
    public  void set(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }
    public void delete(String key){
        redisTemplate.delete(key);
    }
    public Boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ============ Set 操作（点赞用） ============

    // 添加到 Set
    public void sAdd(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    // 从 Set 移除
    public void sRemove(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    // 判断是否在 Set 中
    public boolean sIsMember(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    // 获取 Set 大小
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
}
