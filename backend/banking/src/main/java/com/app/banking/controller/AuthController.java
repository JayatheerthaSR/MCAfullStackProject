package com.app.banking.controller;

import com.app.banking.entity.User;
import com.app.banking.service.CustomerService;
import com.app.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        String username = registrationData.get("username");
        String password = registrationData.get("password");
        String roleStr = registrationData.get("role");
        String firstName = registrationData.get("firstName");
        String lastName = registrationData.get("lastName");
        String emailAddress = registrationData.get("emailAddress");
        String phoneNumber = registrationData.get("phoneNumber");
        String address = registrationData.get("address");

        if (username == null || password == null || roleStr == null) {
            return new ResponseEntity<>("Username, password, and role are required", HttpStatus.BAD_REQUEST);
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid role specified", HttpStatus.BAD_REQUEST);
        }

        try {
            User newUser = userService.registerUser(username, password, role);
            if (role == User.Role.CUSTOMER) {
                if (firstName == null || lastName == null || emailAddress == null) {
                    return new ResponseEntity<>("First name, last name, and email are required for customer registration", HttpStatus.BAD_REQUEST);
                }
                customerService.createCustomer(newUser, firstName, lastName, emailAddress, phoneNumber, address);
            }
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Username already exists
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        // Spring Security's SecurityFilterChain will intercept this request.
        // If authentication (using HTTP Basic Auth headers) is successful,
        // the request will proceed, and we can return a success response.
        // If authentication fails, Spring Security will return a 401 Unauthorized.
        return ResponseEntity.ok("Login successful");
    }
}