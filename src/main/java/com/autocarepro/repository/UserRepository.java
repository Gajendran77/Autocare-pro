package com.autocarepro.repository;

import com.autocarepro.entity.Role;
import com.autocarepro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User> findByResetToken(String resetToken);
    long countByRole(Role role);
}
