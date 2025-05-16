package com.app.banking.controller;

import com.app.banking.entity.Account;
import com.app.banking.repository.AccountRepository; // Import AccountRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('CUSTOMER')") // Adjust authorization as needed
public class AccountBalanceController {

    @Autowired
    private AccountRepository accountRepository; // Inject AccountRepository

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getAccountBalanceByNumber(@PathVariable String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null); // Use the repository directly
        if (account != null) {
            return ResponseEntity.ok(account.getBalance());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}