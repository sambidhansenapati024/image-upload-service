package com.example.demo.service.reids;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> void save(String key, T value, long timeoutSeconds) {

        redisTemplate.opsForValue().set(
                key,
                value,
                Duration.ofSeconds(timeoutSeconds)
        );

    }

    @Override
    public <T> T get(String key, Class<T> clazz) {

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return clazz.cast(value);
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);

    }

    @Override
    public boolean exists(String key) {

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));

    }


    @Override
    public long getRemainingTtl(String key) {

        Long ttl = redisTemplate.getExpire(
                key,
                TimeUnit.SECONDS
        );

        return ttl == null ? 0 : ttl;

    }
}
