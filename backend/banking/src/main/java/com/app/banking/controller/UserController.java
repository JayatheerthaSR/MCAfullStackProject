package com.app.banking.controller;

import com.app.banking.entity.User;
import com.app.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
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
        updatedUser.setPhoneNumber(updateData.get("phoneNumber"));
        updatedUser.setAddress(updateData.get("address"));
        User savedUser = userService.updateUser(existingUser, updatedUser);
        return ResponseEntity.ok(savedUser);
    }
}