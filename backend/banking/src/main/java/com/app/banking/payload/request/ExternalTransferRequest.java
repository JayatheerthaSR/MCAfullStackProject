package com.app.banking.payload.request;

import com.app.banking.entity.TransactionType;

public class ExternalTransferRequest {
    private String sourceAccountNumber;
    private String beneficiaryAccountNumber;
    private Double amount;
    private String description;
    private TransactionType transferType; // Added transferType field

    public ExternalTransferRequest() {
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransactionType transferType) {
        this.transferType = transferType;
    }

    @Override
    public String toString() {
        return "ExternalTransferRequest{" +
                "sourceAccountNumber='" + sourceAccountNumber + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transferType=" + transferType + // Include transferType in toString
                '}';
    }
}