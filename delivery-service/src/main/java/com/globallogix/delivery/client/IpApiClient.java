package com.globallogix.delivery.client;


import com.globallogix.delivery.exceptions.custom_exceptions.IpApiException;
import jakarta.persistence.criteria.Root;
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
        log.info("Trying to get location by ip with url: {}", url);
        try {
            log.info("Starting request to ip-api");
            IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);
            log.info("Got response from ip-api");
            if (response != null && "success".equals(response.getStatus())) {
                log.info("Got success response from ip-api for user: {}", userId);
                return Optional.of(response);
            } else if (response != null && "fail".equals(response.getStatus())){
                log.info("Got fail response from ip-api for user: {}", userId);
                return Optional.of(response);
            }
        } catch (Exception e){
            log.error("Failed to load geodata for the user with id: {}", userId);
        }
        log.info("Response from ip-api is empty");
        return Optional.empty();
    }
}
