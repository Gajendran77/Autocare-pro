package com.autocarepro.service;

import com.autocarepro.dto.RegisterRequest;
import com.autocarepro.entity.Role;
import com.autocarepro.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(RegisterRequest request);
    Optional<User> findByEmail(String email);
    User getById(Long id);
    List<User> findByRole(Role role);
    List<User> findAllCustomers();
    List<User> findAllMechanics();
    User save(User user);
    void initiatePasswordReset(String email);
    boolean resetPassword(String token, String newPassword);
    void toggleEnabled(Long userId);
    long countByRole(Role role);
}
