package com.globallogix.flight.client;


import com.globallogix.flight.dto.DeliveryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "delivery-service",
        url = "http://delivery-service:8080"
)
public interface DeliveryClient {
    @GetMapping("/api/deliveries/available")
    List<DeliveryDto> getAvailableDeliveries();

    @GetMapping("/api/deliveries/{id}")
    DeliveryDto getDelivery(@PathVariable("id") Long id);
}
