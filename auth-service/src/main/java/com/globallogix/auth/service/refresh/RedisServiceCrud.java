package com.globallogix.auth.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceCrud {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "refresh:";


    public void saveRefreshToCache(String token, String username){
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + username, token, Duration.ofDays(7));
        log.info("Refresh token saved to cache successfully");
    }


    public void deleteTokenFromCache(String username){
        Boolean deleted = redisTemplate.delete(CACHE_KEY_PREFIX + username);
        if (deleted) {
            log.info("Refresh token deleted from cache for user: {}", username);
        } else {
            log.warn("Refresh token not found in cache for user: {}", username);
        }
    }

    public Optional<String> getRefreshTokenFromCache(String username){
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(CACHE_KEY_PREFIX+username)
        );
    };
}
