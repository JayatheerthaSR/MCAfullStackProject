package com.app.banking.repository;

import com.app.banking.entity.User;
import com.app.banking.entity.Role; // Adjust import if needed
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email); // Add this
    Optional<User> findByResetTokenAndResetTokenExpiryAfter(String token, LocalDateTime expiry); // Add this
    List<User> findByRole(Role role);
    Boolean existsByUsername(String username); // Optional: If you need to check if a username exists
    Boolean existsByEmail(String email);     // Optional: If you need to check if an email exists
}