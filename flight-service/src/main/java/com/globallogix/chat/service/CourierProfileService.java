package com.globallogix.chat.service;


import com.globallogix.chat.dto.CreateCourierProfileRequest;
import com.globallogix.chat.entity.CourierProfile;
import com.globallogix.chat.exception.custom_exceptions.ProfileNotFoundException;
import com.globallogix.chat.repository.CourierProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierProfileService {
    private final CourierProfileRepository courierProfileRepository;

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
