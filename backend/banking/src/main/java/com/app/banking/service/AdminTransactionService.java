package com.app.banking.service;

import com.app.banking.entity.Transaction;
import com.app.banking.payload.response.AdminTransactionResponse;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.specification.TransactionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminTransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Page<AdminTransactionResponse> getAllTransactions(Pageable pageable, String transactionType, String startDate, String endDate, String accountNumber) {
        Specification<Transaction> spec = Specification.where(null);

        if (transactionType != null && !transactionType.isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasTransactionType(transactionType));
        }

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            try {
                LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
                LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
                spec = spec.and(TransactionSpecifications.hasDateRange(start, end));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (accountNumber != null && !accountNumber.isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasAccountByTransactionType(accountNumber));
        }

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<AdminTransactionResponse> adminTransactionResponseList = transactionPage.getContent().stream()
                .map(transaction -> {
                    AdminTransactionResponse response = new AdminTransactionResponse();
                    response.setTransactionId(transaction.getTransactionId());
                    response.setType(transaction.getTransactionType());
                    response.setAmount(transaction.getAmount());
                    response.setDate(transaction.getTransactionDate());
                    response.setDescription(transaction.getDescription());
                    response.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
                    response.setSenderAccountNumber(transaction.getSenderAccountNumber());
                    response.setCustomerId(transaction.getCustomer().getCustomerId());
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(adminTransactionResponseList, pageable, transactionPage.getTotalElements());
    }

    public Optional<AdminTransactionResponse> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    AdminTransactionResponse response = new AdminTransactionResponse();
                    response.setTransactionId(transaction.getTransactionId());
                    response.setType(transaction.getTransactionType());
                    response.setAmount(transaction.getAmount());
                    response.setDate(transaction.getTransactionDate());
                    response.setDescription(transaction.getDescription());
                    response.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
                    response.setSenderAccountNumber(transaction.getSenderAccountNumber());
                    response.setCustomerId(transaction.getCustomer().getCustomerId());
                    return response;
                });
    }
}