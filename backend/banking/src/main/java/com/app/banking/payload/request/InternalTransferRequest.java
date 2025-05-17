package com.app.banking.payload.request;

import java.math.BigDecimal;

public class InternalTransferRequest extends TransferRequest {
    private String sourceAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount; // Changed to BigDecimal
    private String description;
    private String transferType;

    public InternalTransferRequest() {
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public BigDecimal getAmount() { // Return type is now BigDecimal
        return amount;
    }

    public void setAmount(BigDecimal amount) { // Parameter type is now BigDecimal
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}