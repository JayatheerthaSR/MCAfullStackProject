package com.app.banking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.Role;
import com.app.banking.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByResetTokenAndResetTokenExpiryAfter(String token, LocalDateTime expiry);
    List<User> findByRole(Role role);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}