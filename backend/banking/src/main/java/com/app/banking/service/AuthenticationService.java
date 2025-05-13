package com.app.banking.service;

import com.app.banking.entity.Customer;
import com.app.banking.entity.User;
import com.app.banking.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;

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
        User savedUser = userService.registerUser(newUser);

        if (role == Role.CUSTOMER) {
            // Generate a unique account number (you might want a more robust generation strategy)
            String accountNumber = generateAccountNumber();
            customerService.createCustomer(savedUser, accountNumber);
        }

        return savedUser;
    }

    // Simple random account number generation (for demonstration)
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}