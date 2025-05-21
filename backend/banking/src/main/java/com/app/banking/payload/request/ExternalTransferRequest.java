package com.app.banking.payload.request;

import java.math.BigDecimal;

public class ExternalTransferRequest extends TransferRequest {
    private String sourceAccountNumber;
    private String beneficiaryAccountNumber;
    private BigDecimal amount;
    private String description;
    private String transferType;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    @Override
    public String toString() {
        return "ExternalTransferRequest{" +
                "sourceAccountNumber='" + sourceAccountNumber + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transferType='" + transferType + '\'' +
                '}';
    }
}