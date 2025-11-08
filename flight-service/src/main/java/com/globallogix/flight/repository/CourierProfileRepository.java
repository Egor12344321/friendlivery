package com.globallogix.flight.repository;

import com.globallogix.flight.entity.CourierProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CourierProfileRepository extends JpaRepository<CourierProfile, Long> {
    Optional<CourierProfile> findByUserId(Long userId);

}
