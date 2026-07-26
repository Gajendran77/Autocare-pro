package com.autocarepro.service;

import com.autocarepro.dto.VehicleRequest;
import com.autocarepro.entity.User;
import com.autocarepro.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle addVehicle(User owner, VehicleRequest request);
    List<Vehicle> getVehiclesForOwner(User owner);
    Vehicle getById(Long id);
    void delete(Long id);
    long countForOwner(Long ownerId);
}
