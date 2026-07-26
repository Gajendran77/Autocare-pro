package com.autocarepro.repository;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.BookingStatus;
import com.autocarepro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerOrderByCreatedAtDesc(User customer);
    List<Booking> findByMechanicOrderByCreatedAtDesc(User mechanic);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);
    long countByStatus(BookingStatus status);
    long countByCustomerIdAndStatus(Long customerId, BookingStatus status);
    long countByCustomerId(Long customerId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customer.id = :customerId AND b.preferredDate >= CURRENT_DATE")
    long countUpcomingByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findAllOrderByCreatedAtDesc();

    @Query("SELECT SUM(b.finalCost) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double sumRevenue();

    @Query("SELECT SUM(b.finalCost) FROM Booking b WHERE b.status = 'COMPLETED' AND b.completedAt >= :from")
    Double sumRevenueSince(@Param("from") LocalDateTime from);
}
