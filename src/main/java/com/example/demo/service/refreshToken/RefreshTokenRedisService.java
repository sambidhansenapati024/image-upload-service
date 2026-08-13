package com.example.demo.service.refreshToken;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenRedisService {

    private static final String PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenRedisService(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void store(
            String jti,
            String sessionId,
            Duration expiration
    ) {

        String key = PREFIX + jti;

        redisTemplate.opsForValue()
                .set(
                        key,
                        sessionId,
                        expiration
                );
    }

    public String getSessionId(String jti) {

        return redisTemplate.opsForValue()
                .get(PREFIX + jti);
    }

    public boolean exists(String jti) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + jti)
        );
    }

    public void delete(String jti) {

        redisTemplate.delete(PREFIX + jti);
    }

}
