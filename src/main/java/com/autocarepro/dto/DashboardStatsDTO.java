package com.autocarepro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long activeServices;
    private long completedServices;
    private long upcomingAppointments;
    private long totalVehicles;
}
