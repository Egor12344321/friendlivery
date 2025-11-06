package com.globallogix.flight.controller;


import com.globallogix.flight.DeliveryDto;
import com.globallogix.flight.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;

    @GetMapping("/deliveries")
    public List<DeliveryDto> getMatchingDeliveries(@RequestHeader("X-User-Id") Long courierId){
        return matchingService.findMatchingDeliveries(courierId);
    }
}
