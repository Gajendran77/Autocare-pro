package com.autocarepro.service.impl;

import com.autocarepro.dto.BookingRequest;
import com.autocarepro.dto.DashboardStatsDTO;
import com.autocarepro.entity.*;
import com.autocarepro.exception.BusinessException;
import com.autocarepro.repository.BookingRepository;
import com.autocarepro.repository.UserRepository;
import com.autocarepro.service.BookingService;
import com.autocarepro.service.NotificationService;
import com.autocarepro.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Booking createBooking(User customer, BookingRequest request) {
        Booking booking = Booking.builder()
                .customer(customer)
                .vehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()))
                .ownerName(request.getOwnerName())
                .phone(request.getPhone())
                .vehicleNumber(request.getVehicleNumber().toUpperCase())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .problemDescription(request.getProblemDescription())
                .preferredDate(request.getPreferredDate())
                .pickupRequired(request.isPickupRequired())
                .status(BookingStatus.PENDING)
                .progressPercent(0)
                .build();

        Booking saved = bookingRepository.save(booking);

        notificationService.notify(customer,
                "Your service booking " + saved.getBookingCode() + " has been received and is pending confirmation.",
                "BOOKING");

        // Notify all admins
        userRepository.findByRole(Role.ADMIN).forEach(admin ->
                notificationService.notify(admin,
                        "New booking " + saved.getBookingCode() + " from " + customer.getFullName(), "INFO"));

        return saved;
    }

    @Override
    public List<Booking> getForCustomer(User customer) {
        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Override
    public List<Booking> getForMechanic(User mechanic) {
        return bookingRepository.findByMechanicOrderByCreatedAtDesc(mechanic);
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<Booking> getByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Booking not found"));
    }

    @Override
    @Transactional
    public Booking assignMechanic(Long bookingId, Long mechanicId) {
        Booking booking = getById(bookingId);
        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new BusinessException("Mechanic not found"));

        booking.setMechanic(mechanic);
        booking.setStatus(BookingStatus.ASSIGNED);
        Booking saved = bookingRepository.save(booking);

        notificationService.notify(mechanic,
                "You have been assigned to job " + booking.getBookingCode(), "INFO");
        notificationService.notify(booking.getCustomer(),
                "A mechanic has been assigned to your booking " + booking.getBookingCode(), "BOOKING");

        return saved;
    }

    @Override
    @Transactional
    public Booking updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = getById(bookingId);
        booking.setStatus(status);
        if (status == BookingStatus.COMPLETED) {
            booking.setCompletedAt(LocalDateTime.now());
            booking.setProgressPercent(100);
            if (booking.getFinalCost() == null && booking.getEstimatedCost() != null) {
                booking.setFinalCost(booking.getEstimatedCost());
            }
        }
        Booking saved = bookingRepository.save(booking);

        notificationService.notify(booking.getCustomer(),
                "Your service " + booking.getBookingCode() + " status changed to " + status.name(), "BOOKING");

        return saved;
    }

    @Override
    @Transactional
    public Booking updateProgress(Long bookingId, int percent, String notes) {
        Booking booking = getById(bookingId);
        booking.setProgressPercent(Math.max(0, Math.min(100, percent)));
        if (notes != null && !notes.isBlank()) {
            booking.setServiceNotes(notes);
        }
        if (booking.getProgressPercent() > 0 && booking.getStatus() == BookingStatus.ASSIGNED) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
        }
        if (booking.getProgressPercent() == 100) {
            booking.setStatus(BookingStatus.COMPLETED);
            booking.setCompletedAt(LocalDateTime.now());
        }
        Booking saved = bookingRepository.save(booking);

        notificationService.notify(booking.getCustomer(),
                "Service progress for " + booking.getBookingCode() + " updated to " + booking.getProgressPercent() + "%",
                "BOOKING");

        return saved;
    }

    @Override
    public DashboardStatsDTO getCustomerStats(User customer) {
        long active = bookingRepository.countByCustomerIdAndStatus(customer.getId(), BookingStatus.IN_PROGRESS)
                + bookingRepository.countByCustomerIdAndStatus(customer.getId(), BookingStatus.ASSIGNED)
                + bookingRepository.countByCustomerIdAndStatus(customer.getId(), BookingStatus.CONFIRMED)
                + bookingRepository.countByCustomerIdAndStatus(customer.getId(), BookingStatus.PENDING);
        long completed = bookingRepository.countByCustomerIdAndStatus(customer.getId(), BookingStatus.COMPLETED);
        long upcoming = bookingRepository.countUpcomingByCustomer(customer.getId());

        return DashboardStatsDTO.builder()
                .activeServices(active)
                .completedServices(completed)
                .upcomingAppointments(upcoming)
                .totalVehicles(0) // populated by controller from VehicleService
                .build();
    }
}
