package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AdRecordRedisCounter {
    @Value("${advertisement.daily-limit}")
    private int dailyLimit;

    private static final String KEY_PREFIX = "user:ad:views:";
    private static final long TTL_SECONDS = 86400; // 24小时

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    public boolean canWatchAd(Integer userId) {
        String key = KEY_PREFIX + userId;

        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, dailyLimit, TTL_SECONDS);
            return true;
        }
        Integer remaining = redisTemplate.opsForValue().get(key);
        return remaining > 0;

    }

    //递减
    public void decrementViews(Integer userId) {
        String key = KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, redisTemplate.opsForValue().get(key) - 1);
    }

    public int getRemainingViews(Integer userId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(KEY_PREFIX + userId)
        ).orElse(0);
    }
}
