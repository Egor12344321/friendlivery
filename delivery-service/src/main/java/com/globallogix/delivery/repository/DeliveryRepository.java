package com.globallogix.delivery.repository;

import com.globallogix.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findBySenderId(Long senderId);
    List<Delivery> findByStatusAndCourierIdIsNull(com.globallogix.delivery.entity.DeliveryStatus status);
}
