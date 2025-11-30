package com.globallogix.flight.service;


import com.globallogix.flight.dto.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.exception.custom_exceptions.ProfileNotFoundException;
import com.globallogix.flight.repository.CourierProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierProfileService {
    private final CourierProfileRepository courierProfileRepository;

    @Transactional
    public CourierProfile createOrUpdateProfile(Long userId, CreateCourierProfileRequest request){
        log.debug("FLIGHT-SERVICE: Creation courier profile started");
        CourierProfile profile = courierProfileRepository.findById(userId)
                .orElse(CourierProfile.builder().userId(userId).build());
        profile.setMaxWeight(request.getMaxWeight());
        profile.setPreferredAirlines(request.getPreferredAirlines());
        profile.setNotificationEnabled(request.isNotificationEnabled());
        profile.setContactPreference(request.getContactPreference());

        return courierProfileRepository.save(profile);
    }

    public CourierProfile getProfile(Long userId){
        return courierProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Профиль с id: " + userId + " не найден"));
    }
}
