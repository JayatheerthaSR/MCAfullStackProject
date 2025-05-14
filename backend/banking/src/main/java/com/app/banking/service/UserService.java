package com.app.banking.service;

import com.app.banking.entity.User;
import com.app.banking.entity.Role;
import com.app.banking.payload.request.ForgotPasswordRequest; // Import the DTO
import com.app.banking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    // In a real application, you would autowire an EmailService here
    // private final EmailService emailService;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
        
        
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User existingUser, User updatedUser) {
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setAddress(updatedUser.getAddress());
        return userRepository.save(existingUser);
    }

    public List<User> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    @Transactional
    public void processForgotPasswordRequest(ForgotPasswordRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        userOptional.ifPresent(user -> {
            // Generate a unique reset token
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(2)); // Token valid for 2 hours
            userRepository.save(user);

            // Send an email with the reset link
            String resetLink = "http://localhost:3000/reset-password/" + token; // Adjust your frontend URL
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
        // Do not reveal if the user exists or not for security reasons, so we don't handle the empty Optional
        System.out.println("Password reset requested for username/email: " + usernameOrEmail);
    }

    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now());
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}