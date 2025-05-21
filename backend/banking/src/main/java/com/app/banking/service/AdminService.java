package com.app.banking.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.entity.Role;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .collect(Collectors.toList());
    }

    public Optional<User> getCustomerById(Long customerId) {
        return userRepository.findById(customerId)
                .filter(user -> user.getRole() == Role.CUSTOMER);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}