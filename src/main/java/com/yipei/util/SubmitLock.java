package com.yipei.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** Redis 防重复提交锁 */
@Component
public class SubmitLock {
    private final StringRedisTemplate redisTemplate;

    public SubmitLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 尝试获取锁，成功返回 true */
    public boolean tryLock(String prefix, Long userId, long timeoutSeconds) {
        String key = "submit_lock:" + prefix + ":" + userId;
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", timeoutSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(ok);
    }

    /** 释放锁 */
    public void unlock(String prefix, Long userId) {
        String key = "submit_lock:" + prefix + ":" + userId;
        redisTemplate.delete(key);
    }
}
