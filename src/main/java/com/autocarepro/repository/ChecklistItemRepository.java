package com.autocarepro.repository;

import com.autocarepro.entity.Booking;
import com.autocarepro.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    List<ChecklistItem> findByBooking(Booking booking);
}
