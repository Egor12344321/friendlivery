package com.globallogix.flight.controller;


import com.globallogix.flight.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.service.CourierProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierProfileController {
    private final CourierProfileService courierProfileService;

    @PostMapping("/profile")
    public CourierProfile createProfile(@RequestHeader("X-User-Id") Long userId, @RequestBody CreateCourierProfileRequest request){
        return courierProfileService.createOrUpdateProfile(userId, request);
    }
    @GetMapping("/profile")
    public CourierProfile getProfile(@RequestHeader("X-User-Id") Long userId) {
        return courierProfileService.getProfile(userId);
    }

}
