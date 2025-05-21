package com.app.banking.payload.response;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.app.banking.entity.TransactionType;

public class TransactionResponse {
    private List<TransactionItem> transactions;
    private BigDecimal accountBalance;

    public TransactionResponse() {
    }

    public TransactionResponse(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }

    public TransactionResponse(BigDecimal accountBalance, List<TransactionItem> transactions) {
        this.accountBalance = accountBalance;
        this.transactions = transactions;
    }

    public List<TransactionItem> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public static class TransactionItem {
        private Long transactionId;
        private TransactionType type;
        private BigDecimal amount;
        private String date;
        private String description;
        private String fromAccount;
        private String toAccount;
        private String displayBeneficiaryName;


        public TransactionItem(com.app.banking.entity.Transaction transaction) {
            this.transactionId = transaction.getTransactionId();
            this.type = transaction.getTransactionType();
            this.amount = transaction.getAmount();
            this.date = transaction.getTransactionDate() != null ?
                    transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            this.description = transaction.getDescription();
            this.fromAccount = transaction.getSenderAccountNumber();
            this.toAccount = transaction.getReceiverAccountNumber();

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) { 
                this.displayBeneficiaryName = transaction.getReceiverAccountNumber();
            } else { 
                this.displayBeneficiaryName = transaction.getSenderAccountNumber();
            }
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

        public String getToAccount() {
            return toAccount;
        }

        public void setToAccount(String toAccount) {
            this.toAccount = toAccount;
        }

        public String getDisplayBeneficiaryName() {
            return displayBeneficiaryName;
        }

        public void setDisplayBeneficiaryName(String displayBeneficiaryName) {
            this.displayBeneficiaryName = displayBeneficiaryName;
        }

    }
}