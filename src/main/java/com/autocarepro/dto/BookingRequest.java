package com.autocarepro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotBlank
    private String vehicleType; // CAR, BIKE, TRUCK

    @NotBlank
    private String ownerName;

    @NotBlank
    private String phone;

    @NotBlank
    private String vehicleNumber;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    private Integer year;

    private String problemDescription;

    @NotNull
    private LocalDate preferredDate;

    private boolean pickupRequired;
}
