package com.globallogix.service;


import com.globallogix.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailCacheService {
    private final UserClient userClient;

    @Cacheable(cacheNames = "email", key = "#id")
    public String getEmail(Long id){
        log.info("Cache miss for user: {}", id);
        String senderEmail = userClient.getUserEmail(id);
        log.info("Sender email was added to cache: {}", senderEmail);
        return senderEmail;
    }
}
