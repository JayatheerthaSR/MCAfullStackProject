package com.app.banking.payload.response;

import com.app.banking.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionResponse {
    private BigDecimal initialBalance;
    private List<TransactionItem> transactions;

    public TransactionResponse() {
    }

    public TransactionResponse(BigDecimal initialBalance2, List<TransactionItem> transactions) {
        this.initialBalance = initialBalance2;
        this.transactions = transactions;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public List<TransactionItem> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }

    public static class TransactionItem {
        private Long transactionId;
        private TransactionType type;
        private BigDecimal amount;
        private String date;
        private String description;
        private String fromAccount;
        private String beneficiaryAccountNumber;
        private String beneficiaryName;

        public TransactionItem(com.app.banking.entity.Transaction transaction) {
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

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
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
}