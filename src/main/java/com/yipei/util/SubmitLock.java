package com.yipei.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** Redis 防重复提交锁，Redis 不可用时自动降级允许通过 */
@Component
public class SubmitLock {
    private final StringRedisTemplate redisTemplate;
    private volatile Boolean redisAvailable = null;

    public SubmitLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String prefix, Long userId, long timeoutSeconds) {
        if (!isRedisAvailable()) return true;
        try {
            String key = "submit_lock:" + prefix + ":" + userId;
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", timeoutSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) {
            redisAvailable = false;
            return true;
        }
    }

    public void unlock(String prefix, Long userId) {
        if (!isRedisAvailable()) return;
        try {
            redisTemplate.delete("submit_lock:" + prefix + ":" + userId);
        } catch (Exception ignored) { redisAvailable = false; }
    }

    private boolean isRedisAvailable() {
        if (redisAvailable == null) {
            try {
                redisTemplate.getConnectionFactory().getConnection().ping();
                redisAvailable = true;
            } catch (Exception e) {
                redisAvailable = false;
            }
        }
        return redisAvailable;
    }
}
