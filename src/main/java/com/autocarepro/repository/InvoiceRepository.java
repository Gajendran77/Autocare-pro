package com.autocarepro.repository;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBooking(Booking booking);
}
