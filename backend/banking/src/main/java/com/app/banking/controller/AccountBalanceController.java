package com.app.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.Account;
import com.app.banking.repository.AccountRepository;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('CUSTOMER')")
public class AccountBalanceController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getAccountBalanceByNumber(@PathVariable String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account != null) {
            return ResponseEntity.ok(account.getBalance());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}