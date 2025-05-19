package com.app.banking.service;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
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

    public TransactionResponse getTransactionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerService.findCustomerByUserId(userId) // Assuming you have this method in CustomerService
                .orElseThrow(() -> new RuntimeException("Customer not found for this user"));
        List<Transaction> transactions = transactionRepository.findByUser_UserIdOrderByTransactionDateAsc(user.getUserId());

        return buildTransactionResponse(customer, transactions);
    }

    public TransactionResponse getTransactionsByUserId(Customer customer) {
        List<Transaction> transactions = transactionRepository.findByUser_UserIdOrderByTransactionDateAsc(customer.getUser().getUserId());
        return buildTransactionResponse(customer, transactions);
    }

    private TransactionResponse buildTransactionResponse(Customer customer, List<Transaction> transactions) {
        // Fetch the user's account balance
        List<Account> userAccounts = accountRepository.findByCustomers(customer);
        if (userAccounts.isEmpty()) {
            throw new RuntimeException("User account not found");
        }
        // Assuming a user has one primary account for balance retrieval
        BigDecimal initialBalance = userAccounts.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No accounts found for this customer"))
                .getBalance();

        List<TransactionResponse.TransactionItem> transactionItems = transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());

        return new TransactionResponse(initialBalance, transactionItems);
    }

    private TransactionResponse.TransactionItem convertToTransactionItem(Transaction transaction) {
        TransactionResponse.TransactionItem item = new TransactionResponse.TransactionItem(transaction);
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0)  { // Credit
            Account sourceAccount = accountRepository.findByAccountNumber(transaction.getSourceAccountNumber()).orElse(null);
            item.setFromAccount(sourceAccount != null ? sourceAccount.getAccountNumber() : null);
        } else { // Debit
            Account beneficiaryAccount = accountRepository.findByAccountNumber(transaction.getBeneficiaryAccountNumber()).orElse(null);
            item.setBeneficiaryName(beneficiaryAccount != null ? beneficiaryAccount.getAccountNumber() : "-");
        }
        return item;
    }

    public TransactionResponse getAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<TransactionResponse.TransactionItem> transactionItems = allTransactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList());
        return new TransactionResponse(transactionItems);
    }

    public List<TransactionResponse> getTransactionsByUserIdOrderByDateDescending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<Transaction> transactions = transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(user.getUserId());
        return transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList())
                .stream() // New stream to wrap in a single TransactionResponse
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByUserIdOrderByCreatedAtDescending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<Transaction> transactions = transactionRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
        return transactions.stream()
                .map(this::convertToTransactionItem)
                .collect(Collectors.toList())
                .stream() // New stream to wrap in a single TransactionResponse
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
}