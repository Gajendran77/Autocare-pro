package com.autocarepro.repository;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {
    List<ServiceImage> findByBooking(Booking booking);
    List<ServiceImage> findByBookingAndType(Booking booking, String type);
}
