package com.app.banking.service;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    private CustomerService customerService; // Inject CustomerService

    // This method takes userId, finds customer, then fetches transactions
    public TransactionResponse getTransactionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Customer customer = customerService.findCustomerByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));

        // Correctly use customer.getCustomerId() which is the ID of the Customer entity
        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdOrderByTransactionDateAsc(customer.getCustomerId());

        return buildTransactionResponse(customer, transactions);
    }

    // This method is overloaded to take a Customer entity directly
    public TransactionResponse getTransactionsByUserId(Customer customer) {
        // Use customer.getCustomerId() here as well
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
        // The TransactionItem constructor itself handles the logic for fromAccount, toAccount,
        // and displayBeneficiaryName based on the Transaction entity.
        // So, you just need to create a new instance using the constructor.
        return new TransactionResponse.TransactionItem(transaction);
    }

    public TransactionResponse getAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<TransactionResponse.TransactionItem> transactionItems = allTransactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());

        // For getAllTransactions, provide a meaningful initial balance or adjust TransactionResponse constructor
        // if a balance is not applicable for a global view.
        return new TransactionResponse(BigDecimal.ZERO, transactionItems);
    }

    // Consolidated method for fetching transactions with sorting
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

        // Make sure findByCustomer_CustomerIdOrderByCreatedAtDesc exists in your TransactionRepository
        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerIdOrderByCreatedAtDesc(customer.getCustomerId());

        return transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());
    }
}