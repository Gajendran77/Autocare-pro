package com.autocarepro.service.impl;

import com.autocarepro.dto.VehicleRequest;
import com.autocarepro.entity.FuelType;
import com.autocarepro.entity.User;
import com.autocarepro.entity.Vehicle;
import com.autocarepro.entity.VehicleType;
import com.autocarepro.exception.BusinessException;
import com.autocarepro.repository.VehicleRepository;
import com.autocarepro.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    // Default premium placeholder images by vehicle type (Unsplash, royalty-free)
    private static final String DEFAULT_CAR_IMG = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80";
    private static final String DEFAULT_BIKE_IMG = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80";
    private static final String DEFAULT_TRUCK_IMG = "https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?auto=format&fit=crop&w=800&q=80";

    @Override
    @Transactional
    public Vehicle addVehicle(User owner, VehicleRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BusinessException("A vehicle with this registration number already exists.");
        }

        VehicleType type = VehicleType.valueOf(request.getVehicleType().toUpperCase());
        String image = request.getImageUrl();
        if (image == null || image.isBlank()) {
            image = switch (type) {
                case CAR -> DEFAULT_CAR_IMG;
                case BIKE -> DEFAULT_BIKE_IMG;
                case TRUCK -> DEFAULT_TRUCK_IMG;
            };
        }

        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
                .vehicleType(type)
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .registrationNumber(request.getRegistrationNumber().toUpperCase())
                .fuelType(request.getFuelType() != null ? FuelType.valueOf(request.getFuelType().toUpperCase()) : FuelType.PETROL)
                .imageUrl(image)
                .status("ACTIVE")
                .nextServiceDue(LocalDate.now().plusMonths(6))
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getVehiclesForOwner(User owner) {
        return vehicleRepository.findByOwner(owner);
    }

    @Override
    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Vehicle not found"));
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public long countForOwner(Long ownerId) {
        return vehicleRepository.countByOwnerId(ownerId);
    }
}
