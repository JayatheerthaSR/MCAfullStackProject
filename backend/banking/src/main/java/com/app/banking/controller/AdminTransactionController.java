package com.app.banking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.Transaction;
import com.app.banking.payload.response.AdminTransactionResponse;
import com.app.banking.service.AdminTransactionService;
import com.app.banking.specification.TransactionSpecifications;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {

    @Autowired
    private AdminTransactionService adminTransactionService;

    @GetMapping
    public ResponseEntity<Page<AdminTransactionResponse>> getAllTransactions(
            Pageable pageable,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String accountNumber
    ) {
        Specification<Transaction> spec = Specification.where(null);

        if (transactionType != null && !transactionType.isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasTransactionType(transactionType));
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            try {
                LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
                LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);
                LocalDateTime startDateTime = startLocalDate.atStartOfDay();
                LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

                Specification<Transaction> dateRangeSpec = TransactionSpecifications.hasDateRange(startDateTime, endDateTime);
                spec = spec.and(dateRangeSpec);

            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (accountNumber != null && !accountNumber.isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasAccountByTransactionType(accountNumber));
        }

        Page<AdminTransactionResponse> transactions = adminTransactionService.getAllTransactions(pageable, transactionType, startDate, endDate, accountNumber);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<AdminTransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        return adminTransactionService.getTransactionById(transactionId)
                .map(transactionResponse -> new ResponseEntity<>(transactionResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}