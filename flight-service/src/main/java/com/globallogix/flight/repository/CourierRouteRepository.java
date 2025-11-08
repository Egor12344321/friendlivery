package com.globallogix.flight.repository;

import com.globallogix.flight.entity.CourierRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourierRouteRepository extends JpaRepository<CourierRoute, Long> {
    Optional<List<CourierRoute>> findByUserId(Long userId);
}
