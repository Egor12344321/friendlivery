package com.globallogix.flight.repository;

import com.globallogix.flight.entity.CourierRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRouteRepository extends JpaRepository<CourierRoute, Long> {
    List<CourierRoute> findByUserId(Long userId);
}
