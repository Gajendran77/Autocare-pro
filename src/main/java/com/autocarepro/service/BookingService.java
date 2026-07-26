package com.autocarepro.service;

import com.autocarepro.dto.BookingRequest;
import com.autocarepro.entity.Booking;
import com.autocarepro.entity.BookingStatus;
import com.autocarepro.entity.User;
import com.autocarepro.dto.DashboardStatsDTO;

import java.util.List;

public interface BookingService {
    Booking createBooking(User customer, BookingRequest request);
    List<Booking> getForCustomer(User customer);
    List<Booking> getForMechanic(User mechanic);
    List<Booking> getAll();
    List<Booking> getByStatus(BookingStatus status);
    Booking getById(Long id);
    Booking assignMechanic(Long bookingId, Long mechanicId);
    Booking updateStatus(Long bookingId, BookingStatus status);
    Booking updateProgress(Long bookingId, int percent, String notes);
    DashboardStatsDTO getCustomerStats(User customer);
}
