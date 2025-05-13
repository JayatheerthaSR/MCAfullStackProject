package com.app.banking.service;

import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}