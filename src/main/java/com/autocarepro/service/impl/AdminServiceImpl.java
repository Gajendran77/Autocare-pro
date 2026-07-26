package com.autocarepro.service.impl;

import com.autocarepro.dto.AdminStatsDTO;
import com.autocarepro.entity.BookingStatus;
import com.autocarepro.entity.Role;
import com.autocarepro.repository.BookingRepository;
import com.autocarepro.repository.UserRepository;
import com.autocarepro.repository.VehicleRepository;
import com.autocarepro.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    @Override
    public AdminStatsDTO getStats() {
        Double revenue = bookingRepository.sumRevenue();
        return AdminStatsDTO.builder()
                .totalCustomers(userRepository.countByRole(Role.CUSTOMER))
                .totalMechanics(userRepository.countByRole(Role.MECHANIC))
                .totalVehicles(vehicleRepository.count())
                .totalRevenue(revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO)
                .pendingServices(bookingRepository.countByStatus(BookingStatus.PENDING))
                .completedServices(bookingRepository.countByStatus(BookingStatus.COMPLETED))
                .build();
    }
}
