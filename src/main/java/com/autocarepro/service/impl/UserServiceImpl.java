package com.autocarepro.service.impl;

import com.autocarepro.dto.RegisterRequest;
import com.autocarepro.entity.Role;
import com.autocarepro.entity.User;
import com.autocarepro.exception.BusinessException;
import com.autocarepro.repository.UserRepository;
import com.autocarepro.service.NotificationService;
import com.autocarepro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("An account with this email already exists.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            role = Role.CUSTOMER;
        }
        if (role == Role.ADMIN) {
            role = Role.CUSTOMER; // prevent self-registration as admin
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        notificationService.notify(saved, "Welcome to AutoCare Pro, " + saved.getFullName() + "!", "SUCCESS");
        return saved;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> findAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    @Override
    public List<User> findAllMechanics() {
        return userRepository.findByRole(Role.MECHANIC);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email.toLowerCase()).ifPresent(user -> {
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            // In production this token would be emailed via EmailService
        });
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public void toggleEnabled(Long userId) {
        User user = getById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
