package com.app.banking.repository;

import com.app.banking.entity.User;
import com.app.banking.entity.Role; // Adjust import if needed
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Assuming you have this
    List<User> findByRole(Role role); // Add this line
}