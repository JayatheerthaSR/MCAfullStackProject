// src/main/java/com/app/banking/payload/response/BeneficiaryResponse.java
package com.app.banking.payload.response;

import com.app.banking.entity.Beneficiary;
import java.math.BigDecimal;

public class BeneficiaryResponse {
    private Long beneficiaryId;
    private String beneficiaryName;
    private String bankName;
    private String accountNumber;
    private BigDecimal maxTransferLimit;
    private Long customerId; // Or other relevant customer info

    // Constructors
    public BeneficiaryResponse(Beneficiary beneficiary) {
        this.beneficiaryId = beneficiary.getBeneficiaryId();
        this.beneficiaryName = beneficiary.getBeneficiaryName();
        this.bankName = beneficiary.getBankName();
        this.accountNumber = beneficiary.getAccountNumber();
        this.maxTransferLimit = beneficiary.getMaxTransferLimit();
        this.customerId = beneficiary.getCustomer().getCustomerId();
        // You can add more relevant customer details here if needed,
        // but be mindful of not including the entire Customer object
        // to avoid the original problem.
    }

    // Getters
    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getMaxTransferLimit() {
        return maxTransferLimit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    // You can add setters if needed for other purposes, but for a typical
    // response DTO, getters are usually sufficient.
}