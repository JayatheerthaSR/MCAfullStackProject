package com.app.banking.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.app.banking.entity.Transaction;

import jakarta.persistence.criteria.Predicate;

public class TransactionSpecifications {

    public static Specification<Transaction> hasTransactionType(String transactionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<Transaction> hasDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
    }

    public static Specification<Transaction> hasAccountByTransactionType(String accountNumber) {
        return (root, query, criteriaBuilder) -> {
            Predicate isSender = criteriaBuilder.equal(root.get("senderAccountNumber"), accountNumber);
            Predicate isReceiver = criteriaBuilder.equal(root.get("receiverAccountNumber"), accountNumber);
            Predicate isCredit = criteriaBuilder.equal(root.get("transactionType"), "DEPOSIT");
            Predicate isDebit = criteriaBuilder.notEqual(root.get("transactionType"), "DEPOSIT");
            Predicate creditCondition = criteriaBuilder.and(isCredit, isReceiver);
            Predicate debitCondition = criteriaBuilder.and(isDebit, isSender);

            return criteriaBuilder.or(creditCondition, debitCondition);
        };
    }

    public static Specification<Transaction> hasAccountNumber(String accountNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("senderAccountNumber"), accountNumber),
                        criteriaBuilder.equal(root.get("receiverAccountNumber"), accountNumber)
                );
    }
}