package com.globallogix.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "http://auth-service:8080")
public interface UserClient{
    @GetMapping("api/users/{userId}/email")
    String getUserEmail(@PathVariable("userId") Long userId);
}
