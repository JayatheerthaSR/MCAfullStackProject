package com.app.banking.payload.response;

import com.app.banking.entity.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionResponse {
    private Long transactionId;
    private TransactionType type;
    private double amount;
    private String date;
    private String description;
    private String fromAccount;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;

    public TransactionResponse() {
    }

    public TransactionResponse(com.app.banking.entity.Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.type = transaction.getTransactionType();
        this.amount = transaction.getAmount();
        this.date = transaction.getTransactionDate() != null ?
                    transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
        this.description = transaction.getDescription();
        this.beneficiaryAccountNumber = transaction.getBeneficiaryAccountNumber();
        this.fromAccount = transaction.getSourceAccountNumber();
        this.beneficiaryName = transaction.getBeneficiaryAccountNumber() != null ?
                               transaction.getBeneficiaryAccountNumber() : "-";
        // You might need additional logic to fetch the actual beneficiary name
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }
}