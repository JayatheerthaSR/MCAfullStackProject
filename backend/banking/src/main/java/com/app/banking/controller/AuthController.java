package com.app.banking.controller;

import com.app.banking.entity.User;
import com.app.banking.entity.Role;
import com.app.banking.payload.request.ForgotPasswordRequest; // Import DTO
import com.app.banking.payload.request.ResetPasswordRequest; // Import DTO
import com.app.banking.security.jwt.JwtUtil;
import com.app.banking.service.UserService;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        try {
            User user = new User();
            user.setUsername(registrationData.get("username"));
            user.setPassword(passwordEncoder.encode(registrationData.get("password")));
            user.setFirstName(registrationData.get("firstName"));
            user.setLastName(registrationData.get("lastName"));
            user.setEmail(registrationData.get("email"));
            user.setRole(Role.valueOf(registrationData.get("role").toUpperCase()));
            userService.registerUser(user);
            return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            System.out.println("UserDetails after authentication: " + userDetails);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("Retrieved User entity: " + user);
            System.out.println("User Role: " + user.getRole());
            System.out.println("User ID: " + user.getUserId());

            String jwtToken = null;
            try {
                jwtToken = jwtUtil.generateToken(userDetails);
                System.out.println("Generated JWT Token: " + jwtToken);
            } catch (Exception e) {
                System.err.println("Error generating JWT: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>("Error generating token", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok(Map.of("message", "Login successful", "role", user.getRole().name(), "userId", user.getUserId().toString(), "token", jwtToken));

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

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (userService.resetPassword(request.getToken(), request.getNewPassword())) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired reset token.");
        }
    }
}