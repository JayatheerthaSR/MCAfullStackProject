package com.app.banking.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.BeneficiaryRepository;
import com.app.banking.repository.TransactionRepository;

@Service
public class SearchService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Map<String, Object>> searchAll(String query) {
        List<Map<String, Object>> results = new ArrayList<>();

        accountRepository.findByAccountNumberContaining(query).forEach(account -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", "account-" + account.getAccountNumber());
            result.put("entityType", "account");
            result.put("displayText", account.getAccountNumber());
            result.put("accountNumber", account.getAccountNumber());
            results.add(result);
        });

        beneficiaryRepository.findByBeneficiaryNameContainingIgnoreCaseOrAccountNumberContaining(query, query).forEach(beneficiary -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", "beneficiary-" + beneficiary.getBeneficiaryId());
            result.put("entityType", "beneficiary");
            result.put("displayText", beneficiary.getBeneficiaryName() + " (" + beneficiary.getAccountNumber() + ")");
            result.put("beneficiaryId", beneficiary.getBeneficiaryId());
            results.add(result);
        });

        transactionRepository.searchByDescriptionOrTransactionIdString(query).forEach(transaction -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", "transaction-" + transaction.getTransactionId());
            result.put("entityType", "transaction");
            result.put("displayText", transaction.getDescription() + " (ID: " + transaction.getTransactionId() + ")");
            result.put("transactionId", transaction.getTransactionId());
            results.add(result);
        });

        return results;
    }
}