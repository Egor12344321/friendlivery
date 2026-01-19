package com.globallogix.delivery.client;


import com.globallogix.delivery.exceptions.custom_exceptions.IpApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class IpApiClient {
    private final RestTemplate restTemplate;


    public Optional<IpApiResponse> getUserLocationByIp(String ip, String userId){
        String url = "http://ip-api.com/json/" + ip + "?fields=17035263";
        log.info("Trying to get location by ip");
        try {
            IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);
            if (response != null && "success".equals(response.getStatus())) {
                return Optional.of(response);
            }
        } catch (Exception e){
            log.error("Failed to load geodata for the user with id: {}", userId);
        }

        return Optional.empty();

    }
}
