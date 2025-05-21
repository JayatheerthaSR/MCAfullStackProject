package com.app.banking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private CustomerService customerService;
    
    public TransactionResponse getTransactionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Customer customer = customerService.findCustomerByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));

        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdOrderByTransactionDateAsc(customer.getCustomerId());

        return buildTransactionResponse(customer, transactions);
    }
    
    public TransactionResponse getTransactionsByUserId(Customer customer) {
        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdOrderByTransactionDateAsc(customer.getCustomerId());
        return buildTransactionResponse(customer, transactions);
    }

    private TransactionResponse buildTransactionResponse(Customer customer, List<Transaction> transactions) {
        List<Account> userAccounts = accountRepository.findByCustomers(customer);
        if (userAccounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for customer: " + customer.getCustomerId());
        }

        BigDecimal totalBalance = userAccounts.stream()
                                    .map(Account::getBalance)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionResponse.TransactionItem> transactionItems = transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());

        return new TransactionResponse(totalBalance, transactionItems);
    }

    private TransactionResponse.TransactionItem convertToTransactionItem(Transaction transaction) {
        return new TransactionResponse.TransactionItem(transaction);
    }

    public TransactionResponse getAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<TransactionResponse.TransactionItem> transactionItems = allTransactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());
        return new TransactionResponse(BigDecimal.ZERO, transactionItems);
    }

    private List<TransactionResponse.TransactionItem> getTransactionsSorted(Long userId, boolean ascending) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Customer customer = customerService.findCustomerByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));

        List<Transaction> transactions;
        if (ascending) {
            transactions = transactionRepository.findByCustomer_CustomerIdOrderByTransactionDateAsc(customer.getCustomerId());
        } else {
            transactions = transactionRepository.findByCustomer_CustomerIdOrderByTransactionDateDesc(customer.getCustomerId());
        }

        return transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse.TransactionItem> getTransactionsByUserIdOrderByDateDescending(Long userId) {
        return getTransactionsSorted(userId, false);
    }

    public List<TransactionResponse.TransactionItem> getTransactionsByUserIdOrderByCreatedAtDescending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Customer customer = customerService.findCustomerByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));

        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdOrderByCreatedAtDesc(customer.getCustomerId());

        return transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());
    }
}