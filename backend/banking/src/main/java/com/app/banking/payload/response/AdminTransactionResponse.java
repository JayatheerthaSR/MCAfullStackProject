package com.app.banking.payload.response;

import com.app.banking.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminTransactionResponse {
    private Long transactionId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private String receiverAccountNumber;
    private String senderAccountNumber;
    private Long customerId;

    public AdminTransactionResponse() {
    }

    public AdminTransactionResponse(Long transactionId, TransactionType type, BigDecimal amount, LocalDateTime date, String description, String receiverAccountNumber, String senderAccountNumber, Long customerId) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.receiverAccountNumber = receiverAccountNumber;
        this.senderAccountNumber = senderAccountNumber;
        this.customerId = customerId;
    }

    // Getters and Setters (excluding userId)

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}