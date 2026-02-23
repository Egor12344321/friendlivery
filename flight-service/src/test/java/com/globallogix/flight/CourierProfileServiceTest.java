package com.globallogix.flight;


import com.globallogix.flight.dto.CreateCourierProfileRequest;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.repository.CourierProfileRepository;
import com.globallogix.flight.service.CourierProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourierProfileServiceTest {

    @Mock
    private CourierProfileRepository courierProfileRepository;

    @InjectMocks
    private CourierProfileService courierProfileService;

    @Test
    @DisplayName("Создание профиля, если его нет")
    void testCreatingProfileWhenNotFound(){
        Long userId = 1L;
        CreateCourierProfileRequest request = new CreateCourierProfileRequest();
        request.setMaxWeight(10.0);
        request.setContactPreference("TELEGRAM");
        request.setNotificationEnabled(true);
        request.setPreferredAirlines("POBEDA");

        when(courierProfileRepository.findById(userId)).thenReturn(Optional.empty());
        when(courierProfileRepository.save(any(CourierProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CourierProfile res = courierProfileService.createOrUpdateProfile(userId, request);

        verify(courierProfileRepository).save(any(CourierProfile.class));
        assertThat(res).isNotNull();
        assertThat(res.getMaxWeight()).isEqualTo(10.0);
        assertThat(res.getContactPreference()).isEqualTo("TELEGRAM");
        assertThat(res.getNotificationEnabled()).isTrue();
        assertThat(res.getPreferredAirlines()).isEqualTo("POBEDA");
    }

    @Test
    @DisplayName("Редактирование профиля")
    void testUpdatingProfileWhenItExists(){
        Long userId = 1L;
        CreateCourierProfileRequest request = new CreateCourierProfileRequest();
        request.setMaxWeight(10.0);
        request.setContactPreference("TELEGRAM");
        request.setNotificationEnabled(true);
        request.setPreferredAirlines("POBEDA");

        CourierProfile oldProfile = new CourierProfile();
        oldProfile.setMaxWeight(11.0);
        oldProfile.setContactPreference("MAIL");
        oldProfile.setNotificationEnabled(false);
        oldProfile.setPreferredAirlines("AEROFLOT");

        CourierProfile newProfile = new CourierProfile();
        newProfile.setMaxWeight(10.0);
        newProfile.setContactPreference("TELEGRAM");
        newProfile.setNotificationEnabled(true);
        newProfile.setPreferredAirlines("POBEDA");

        when(courierProfileRepository.findById(userId)).thenReturn(Optional.of(oldProfile));
        when(courierProfileRepository.save(any(CourierProfile.class))).thenReturn(newProfile);

        CourierProfile res = courierProfileService.createOrUpdateProfile(userId, request);

        verify(courierProfileRepository).save(any(CourierProfile.class));
        assertThat(res).isNotNull();
        assertThat(res.getMaxWeight()).isEqualTo(10.0);
        assertThat(res.getContactPreference()).isEqualTo("TELEGRAM");
        assertThat(res.getNotificationEnabled()).isTrue();
        assertThat(res.getPreferredAirlines()).isEqualTo("POBEDA");
    }
}
