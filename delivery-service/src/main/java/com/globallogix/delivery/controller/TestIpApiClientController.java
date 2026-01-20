package com.globallogix.delivery.controller;


import com.globallogix.delivery.annotations.GeoAudit;
import com.globallogix.delivery.client.IpApiClient;
import com.globallogix.delivery.client.IpApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/geo")
public class TestIpApiClientController {

    private final IpApiClient ipApiClient;

    @GetMapping
    public ResponseEntity<?> getUserLocation(@RequestHeader("X-Client-IP") String ip){
        Optional<IpApiResponse> response = ipApiClient.getUserLocationByIp(ip, "1");
        log.info("Response: {}", response);
        if (response.isPresent()){
            return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
