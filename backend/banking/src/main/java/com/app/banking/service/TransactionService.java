package com.app.banking.service;

import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.payload.response.TransactionResponse; // Import TransactionResponse
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByUserIdOrderByDateDescending(Long userId) {
        return transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(userId).stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByUserIdOrderByCreatedAtDescending(Long userId) {
        return transactionRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    // This is the method your CustomerController is calling
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(customer.getUserId()).stream()
                .map(TransactionResponse::new) // Convert Transaction to TransactionResponse
                .collect(Collectors.toList());
    }

    // You can add other transaction-related methods here if needed
}