package com.globallogix.flight.controller;


import com.globallogix.flight.dto.CourierRouteRequest;
import com.globallogix.flight.dto.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.service.CourierProfileService;
import com.globallogix.flight.service.CourierRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
@Slf4j
public class CourierProfileController {
    private final CourierProfileService courierProfileService;
    private final CourierRouteService courierRouteService;

    @PostMapping("/profile")
    public CourierProfile createProfile(@RequestHeader("X-User-Id") Long userId, @RequestBody CreateCourierProfileRequest request){
        log.debug("FLIGHT-CONTROLLER: Controller get request to creation courier-profile for user: {}", userId);
        return courierProfileService.createOrUpdateProfile(userId, request);
    }
    @GetMapping("/profile")
    public CourierProfile getProfile(@RequestHeader("X-User-Id") Long userId) {

        return courierProfileService.getProfile(userId);
    }
    @PostMapping("/routes")
    public CourierRoute createRoute(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody CourierRouteRequest request) {
        log.debug("FLIGHT-CONTROLLER: Create rout started");
        return courierRouteService.createRoute(userId, request);
    }
    @GetMapping("/routes")
    public List<CourierRoute> getUserRoutes(@RequestHeader("X-User-Id") Long userId) {
        log.info("FLIGHT-CONTROLLER: Getting user's {} routes started", userId);
        return courierRouteService.getUserRoutes(userId);
    }
}
