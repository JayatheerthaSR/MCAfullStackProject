package com.app.banking.service;

import com.app.banking.entity.User;
import com.app.banking.entity.Role;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    public User register(String username, String password, String firstName, String lastName, String email, Role role) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Password will be encoded in UserService
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setRole(role);

        userService.initiateUserRegistration(newUser); // Corrected method call

        User savedUser = newUser; // The user is saved within initiateUserRegistration

        if (role == Role.CUSTOMER) {
            // Generate a unique account number (you might want a more robust generation strategy)
            String accountNumber = generateAccountNumber();
            customerService.createCustomer(savedUser, accountNumber);
        }

        return savedUser;
    }

    // Simple random account number generation (for demonstration)
    private String generateAccountNumber() {
        Random sb = new Random();
        StringBuilder accountNumberBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNumberBuilder.append(sb.nextInt(10));
        }
        return accountNumberBuilder.toString();
    }
}