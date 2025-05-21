package com.app.banking.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.entity.Role;
import com.app.banking.entity.User;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    public User register(String username, String password, String firstName, String lastName, String email, Role role) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setRole(role);

        userService.initiateUserRegistration(newUser);

        User savedUser = newUser;

        if (role == Role.CUSTOMER) {
            String accountNumber = generateAccountNumber();
            customerService.createCustomer(savedUser, accountNumber);
        }

        return savedUser;
    }

    private String generateAccountNumber() {
        Random sb = new Random();
        StringBuilder accountNumberBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNumberBuilder.append(sb.nextInt(10));
        }
        return accountNumberBuilder.toString();
    }
}