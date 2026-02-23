package com.globallogix.flight;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogix.flight.dto.CourierRouteRequest;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.repository.CourierRouteRepository;
import com.globallogix.flight.service.CourierRouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class CourierRouteServiceTest {

    @Mock
    private CourierRouteRepository courierRouteRepository;

    @InjectMocks
    private CourierRouteService courierRouteService;

    @Test
    void testSaveCourierRoute(){
        CourierRouteRequest request = new CourierRouteRequest("SVO", "OMS", 1, LocalDate.parse("2025-11-11"));
        Long userId = 1L;
        CourierRoute resultRoute = new CourierRoute();

        resultRoute.setFlightDate(LocalDate.parse("2025-11-11"));
        resultRoute.setActive(true);
        resultRoute.setDepartureAirport("SVO");
        resultRoute.setArrivalAirport("OMS");
        resultRoute.setId(1L);
        resultRoute.setUserId(1L);


        when(courierRouteRepository.save(any(CourierRoute.class))).thenAnswer(invocation -> {
            return invocation.<CourierRoute>getArgument(0);});

        CourierRoute result = courierRouteService.createRoute(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getDepartureAirport()).isEqualTo("SVO");
        assertThat(result.getArrivalAirport()).isEqualTo("OMS");
        assertThat(result.getFlightDate()).isEqualTo(LocalDate.of(2025, 11, 11));


        verify(courierRouteRepository, times(1)).save(any(CourierRoute.class));
    }
}
