package com.autocarepro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleRequest {
    @NotBlank
    private String vehicleType;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    private Integer year;
    @NotBlank
    private String registrationNumber;
    private String fuelType;
    private String imageUrl;
}
