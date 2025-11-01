package com.globallogix.flight.service;


import com.globallogix.flight.dto.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.repository.CourierProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourierProfileService {
    private final CourierProfileRepository courierProfileRepository;

    public CourierProfile createOrUpdateProfile(Long userId, CreateCourierProfileRequest request){
        CourierProfile profile = courierProfileRepository.findById(userId)
                .orElse(CourierProfile.builder().userId(userId).build());
        profile.setMaxWeight(request.getMaxWeight());
        profile.setPreferredAirlines(request.getPreferredAirlines());
        profile.setNotificationEnabled(request.isNotificationEnabled());
        profile.setContactPreference(request.getContactPreference());

        return courierProfileRepository.save(profile);
    }

    public CourierProfile getProfile(Long userId){
        return courierProfileRepository.findByUserId(userId);
    }
}
