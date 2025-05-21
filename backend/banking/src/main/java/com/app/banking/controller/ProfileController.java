package com.app.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.User;
import com.app.banking.payload.request.UpdateProfileRequest;
import com.app.banking.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest updatedProfileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User existingUser = userService.findUserByUsername(username);
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User updatedUser = new User();
        updatedUser.setFirstName(updatedProfileRequest.getFirstName());
        updatedUser.setLastName(updatedProfileRequest.getLastName());
        updatedUser.setEmail(updatedProfileRequest.getEmail());
        updatedUser.setPhone_number(updatedProfileRequest.getPhone());
        updatedUser.setAddress(updatedProfileRequest.getAddress());

//        boolean emailChanged = !existingUser.getEmail().equals(updatedUser.getEmail());

        try {
//            User savedUser = userService.updateUser(existingUser, updatedUser, emailChanged);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}