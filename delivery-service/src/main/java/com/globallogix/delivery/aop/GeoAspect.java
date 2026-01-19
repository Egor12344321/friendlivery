package com.globallogix.delivery.aop;


import com.globallogix.delivery.client.IpApiClient;
import com.globallogix.delivery.client.IpApiResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.UserLocations;
import com.globallogix.delivery.repository.UserLocationsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class GeoAspect extends Delivery{

    private final IpApiClient client;
    private final UserLocationsRepository userLocationsRepository;

    @Around("@annotation(com.globallogix.delivery.annotations.GeoAudit)")
    public void getUserLocationById(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        String userId = request.getHeader("X-User-Id");
        CompletableFuture.runAsync(() -> {
            Optional<IpApiResponse> response = client.getUserLocationByIp(ip, userId);
            if (response.isPresent()) {
                UserLocations userLocations = IpApiResponse.mapFromResponseToEntity(response.get());
                userLocationsRepository.save(userLocations);
                log.info("UserLocations saved to db for user with id: {}", userId);
            }
        });


    }

}
