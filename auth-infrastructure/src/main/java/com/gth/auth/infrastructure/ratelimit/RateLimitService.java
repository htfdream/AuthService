package com.gth.auth.infrastructure.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RateLimitService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "ratelimit:";

    public RateLimitService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int limit, Duration window) {
        String redisKey = PREFIX + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if(count == 1) redisTemplate.expire(redisKey, window.getSeconds(), TimeUnit.SECONDS);

        boolean allowed = count <= limit;
        if(!allowed) log.warn("Rate limit exceeded for key: {}, count: {}/{}", key, count, limit);

        return allowed;
    }

    public int getRemaining(String key, int limit) {
        String redisKey = PREFIX + key;
        String countStr = redisTemplate.opsForValue().get(redisKey);

        if(countStr == null) return limit;
        int count = Integer.parseInt(countStr);
        return Math.max(0, limit - count);
    }

    public void delete(String key) {
        redisTemplate.delete(PREFIX + key);
    }
}
