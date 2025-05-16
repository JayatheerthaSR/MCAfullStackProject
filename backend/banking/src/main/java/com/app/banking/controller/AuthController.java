package com.app.banking.controller;

import com.app.banking.entity.User;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Role;
import com.app.banking.payload.request.ForgotPasswordRequest;
import com.app.banking.payload.request.OTPVerificationRequest;
import com.app.banking.payload.request.RegistrationRequest;
import com.app.banking.payload.request.ResetPasswordRequest;
import com.app.banking.security.jwt.JwtUtil;
import com.app.banking.service.CustomerService;
import com.app.banking.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            User user = new User();
            user.setUsername(registrationRequest.getUsername());
            user.setPassword(registrationRequest.getPassword()); // Password will be encoded in UserService
            user.setFirstName(registrationRequest.getFirstName());
            user.setLastName(registrationRequest.getLastName());
            user.setEmail(registrationRequest.getEmail());
            user.setAddress(registrationRequest.getAddress());
            user.setRole(Role.valueOf(registrationRequest.getRole().toUpperCase()));
            user.setPhone_number(registrationRequest.getPhone()); // Add this line to set the phone number
            userService.initiateUserRegistration(user); // Use the new method
            return new ResponseEntity<>("Registration initiated. Please check your email for OTP verification.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody OTPVerificationRequest verificationRequest) {
        if (userService.verifyOTP(verificationRequest.getEmail(), verificationRequest.getOtp())) {
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Customer> customerOptional = customerService.findCustomerByUserId(user.getUserId());

            if (customerOptional.isEmpty()) {
                return new ResponseEntity<>("Customer not found for this user", HttpStatus.NOT_FOUND);
            }
            Customer customer = customerOptional.get();
            Long customerId = customer.getCustomerId();

            String jwtToken = null;
            try {
                jwtToken = jwtUtil.generateToken(userDetails);
            } catch (Exception e) {
                System.err.println("Error generating JWT: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>("Error generating token", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "role", user.getRole().name(),
                    "userId", user.getUserId().toString(),
                    "customerId", customerId.toString(),
                    "token", jwtToken
            ));

        } catch (AuthenticationException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during login: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Login failed due to an internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.processForgotPasswordRequest(request);
        return ResponseEntity.ok("Password reset link sent to your email if the account exists.");
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @Valid @RequestBody ResetPasswordRequest request) {
        if (userService.resetPassword(token, request.getNewPassword())) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired reset token.");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, currentPassword));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Incorrect current password.", HttpStatus.BAD_REQUEST);
        }

        userService.updateUserPassword(user, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }
}