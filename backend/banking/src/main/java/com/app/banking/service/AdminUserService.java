package com.app.banking.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.banking.entity.Role;
import com.app.banking.entity.User;
import com.app.banking.repository.UserRepository;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<User> getAllUsers(Pageable pageable, Specification<User> spec) {
        return userRepository.findAll(spec, pageable);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    public Optional<User> updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    updatedUser.setUserId(userId);
                    updatedUser.setPassword(existingUser.getPassword());
                    return userRepository.save(updatedUser);
                });
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> updateUserStatus(Long userId, boolean isActive) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setActive(isActive);
                    return userRepository.save(user);
                });
    }

    public Optional<User> updateUserRole(Long userId, String role) {
        return userRepository.findById(userId)
                .map(user -> {
                    try {
                        user.setRole(Role.valueOf(role.toUpperCase()));
                        return userRepository.save(user);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                });
    }
}