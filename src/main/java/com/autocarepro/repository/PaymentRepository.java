package com.autocarepro.repository;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.Payment;
import com.autocarepro.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    List<Payment> findByStatus(PaymentStatus status);
}
