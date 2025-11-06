package com.globallogix.payment.repository;


import com.globallogix.payment.entity.Payment;
import com.globallogix.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByDeliveryId(Long deliveryId);
    List<Payment> findBySenderId(Long senderId);
    List<Payment> findByStatus(PaymentStatus status);
}
