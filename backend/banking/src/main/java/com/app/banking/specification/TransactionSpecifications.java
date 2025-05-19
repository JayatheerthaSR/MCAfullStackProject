package com.app.banking.specification;

import com.app.banking.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionSpecifications {

    public static Specification<Transaction> hasTransactionType(String transactionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<Transaction> hasDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null; // No date range specified
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
    }

    // New specification to handle account number based on transaction type
    public static Specification<Transaction> hasAccountByTransactionType(String accountNumber) {
        return (root, query, criteriaBuilder) -> {
            Predicate isSender = criteriaBuilder.equal(root.get("senderAccountNumber"), accountNumber);
            Predicate isReceiver = criteriaBuilder.equal(root.get("receiverAccountNumber"), accountNumber);
            Predicate isCredit = criteriaBuilder.equal(root.get("transactionType"), "DEPOSIT"); // Adjust based on your actual credit type
            Predicate isDebit = criteriaBuilder.notEqual(root.get("transactionType"), "DEPOSIT"); // Adjust based on your actual debit types

            // For credit transactions (like DEPOSIT), the account number should match the receiver
            Predicate creditCondition = criteriaBuilder.and(isCredit, isReceiver);

            // For debit transactions (like WITHDRAWAL, TRANSFER), the account number should match the sender
            Predicate debitCondition = criteriaBuilder.and(isDebit, isSender);

            return criteriaBuilder.or(creditCondition, debitCondition);
        };
    }

    // Existing generic account number filter (you might still want this for a general search)
    public static Specification<Transaction> hasAccountNumber(String accountNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("senderAccountNumber"), accountNumber),
                        criteriaBuilder.equal(root.get("receiverAccountNumber"), accountNumber)
                );
    }

    // Add more specifications for other filter criteria as needed
    // For example: by user ID, by transaction amount range, etc.
}