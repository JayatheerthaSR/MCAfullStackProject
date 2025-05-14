package com.app.banking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 10)
    private double amount;

    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    private String description;

    @Column(name = "beneficiary_account_number")
    private String beneficiaryAccountNumber; // For transfer transactions

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

	public void setUser(User user) {
		this.user = user;
		
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
		
	}

	public void setAmount(double amount) {
		this.amount = amount;
		
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description =description;
		
	}

	public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
		this.beneficiaryAccountNumber = beneficiaryAccountNumber;		
	}
}