package com.autocarepro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalCustomers;
    private long totalMechanics;
    private long totalVehicles;
    private BigDecimal totalRevenue;
    private long pendingServices;
    private long completedServices;
}
