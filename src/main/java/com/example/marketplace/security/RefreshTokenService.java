package com.example.marketplace.security;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private final static long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private final static String REDIS_PREFIX = "refreshToken";

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(REDIS_PREFIX + refreshToken,
                email,
                REFRESH_TOKEN_EXPIRATION_DAYS,
                TimeUnit.DAYS);
        return refreshToken;
    }

    public String getEmailBtyToken(String refreshToken) {
        return redisTemplate.opsForValue().get(REDIS_PREFIX + refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(REDIS_PREFIX + refreshToken);
    }
}
