package com.app.banking.payload.response;

import java.math.BigDecimal;

import com.app.banking.entity.Beneficiary;

public class BeneficiaryResponse {
    private Long beneficiaryId;
    private String beneficiaryName;
    private String bankName;
    private String accountNumber;
    private BigDecimal maxTransferLimit;
    private Long customerId;

    public BeneficiaryResponse(Beneficiary beneficiary) {
        this.beneficiaryId = beneficiary.getBeneficiaryId();
        this.beneficiaryName = beneficiary.getBeneficiaryName();
        this.bankName = beneficiary.getBankName();
        this.accountNumber = beneficiary.getAccountNumber();
        this.maxTransferLimit = beneficiary.getMaxTransferLimit();
        this.customerId = beneficiary.getCustomer().getCustomerId();
    }

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
}