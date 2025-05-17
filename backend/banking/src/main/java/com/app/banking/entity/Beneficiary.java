package com.app.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank
    @Column(name = "beneficiary_name", nullable = false)
    private String beneficiaryName;

    @NotBlank
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @NotBlank
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "ifsc_code") // Add the ifscCode field
    private String ifscCode;

    @Column(name = "max_transfer_limit", precision = 10)
    private BigDecimal maxTransferLimit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Manually add Getters
    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public Customer getCustomer() {
        return customer;
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

    public String getIfscCode() { // Add the getter for ifscCode
        return ifscCode;
    }

    public BigDecimal getMaxTransferLimit() {
        return maxTransferLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Manually add Setters
    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setIfscCode(String ifscCode) { // Add the setter for ifscCode
        this.ifscCode = ifscCode;
    }

    public void setMaxTransferLimit(BigDecimal maxTransferLimit) {
        this.maxTransferLimit = maxTransferLimit;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}