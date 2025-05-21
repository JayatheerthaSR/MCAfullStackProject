package com.app.banking.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.User;
import com.app.banking.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    @Lazy
    private UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> updateData) {
        Optional<User> existingUserOptional = userService.findById(userId);
        if (existingUserOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User existingUser = existingUserOptional.get();
        User updatedUser = new User();
        updatedUser.setFirstName(updateData.get("firstName"));
        updatedUser.setLastName(updateData.get("lastName"));
        updatedUser.setEmail(updateData.get("email"));
        updatedUser.setPhone_number(updateData.get("phoneNumber"));
        updatedUser.setAddress(updateData.get("address"));
        User savedUser = userService.updateUser(existingUser, updatedUser);
        return ResponseEntity.ok(savedUser);
    }
}