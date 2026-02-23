package com.globallogix.flight.controller;


import com.globallogix.flight.dto.DeliveryDto;
import com.globallogix.flight.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@Tag(name = "Matching deliveries")
public class MatchingController {
    private final MatchingService matchingService;

    @GetMapping("/deliveries")
    @Operation(description = "Получить все подходящие заявки на доставку для курьера в соответствии с его созданными маршрутами")
    public List<DeliveryDto> getMatchingDeliveries(@RequestHeader("X-User-Id") Long courierId){
        return matchingService.findMatchingDeliveries(courierId);
    }
}
