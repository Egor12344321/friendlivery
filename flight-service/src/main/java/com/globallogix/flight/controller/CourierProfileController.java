package com.globallogix.flight.controller;


import com.globallogix.flight.dto.CourierRouteRequest;
import com.globallogix.flight.dto.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.service.CourierProfileService;
import com.globallogix.flight.service.CourierRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courier Profile management")
public class CourierProfileController {
    private final CourierProfileService courierProfileService;
    private final CourierRouteService courierRouteService;

    @PostMapping("/profile")
    @Operation(description = "Создать профиля курьера")
    public ResponseEntity<?> createProfile(@RequestHeader("X-User-Id") Long userId, @RequestHeader("X-User-Verification-Status") String verificationStatus, @RequestBody CreateCourierProfileRequest request){
        log.debug("FLIGHT-CONTROLLER: Controller get request to creation courier-profile for user: {}", userId);
        if (verificationStatus.equals("VERIFIED")) {
            return ResponseEntity.ok(courierProfileService.createOrUpdateProfile(userId, request));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("/api/kyc/passport/verification");
        }
    }
    @GetMapping("/profile")
    @Operation(description = "Получить профиль курьера по id")
    public CourierProfile getProfile(@RequestHeader("X-User-Id") Long userId) {

        return courierProfileService.getProfile(userId);
    }
    @PostMapping("/routes")
    @Operation(description = "Создать путь для пользователя по id, по которому он хочет взять посылку")
    public CourierRoute createRoute(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody @Valid CourierRouteRequest request) {
        log.debug("FLIGHT-CONTROLLER: Create rout started");
        return courierRouteService.createRoute(userId, request);
    }
    @GetMapping("/routes")
    @Operation(description = "Получить все созданные пути курьера по id")
    public List<CourierRoute> getUserRoutes(@RequestHeader("X-User-Id") Long userId) {
        log.info("FLIGHT-CONTROLLER: Getting user's {} routes started", userId);
        return courierRouteService.getUserRoutes(userId);
    }
}
