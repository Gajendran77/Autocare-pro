package com.autocarepro.repository;

import com.autocarepro.entity.User;
import com.autocarepro.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwner(User owner);
    List<Vehicle> findByOwnerId(Long ownerId);
    boolean existsByRegistrationNumber(String registrationNumber);
    long countByOwnerId(Long ownerId);
}
