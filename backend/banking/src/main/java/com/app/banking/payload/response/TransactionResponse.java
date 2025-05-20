package com.app.banking.payload.response;

import com.app.banking.entity.TransactionType;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        private String fromAccount; // Represents the sender's account for this transaction
        private String toAccount;   // Represents the receiver's account for this transaction
        private String displayBeneficiaryName; // A more general name for the counterparty (e.g., beneficiary name or sender's account if it's a credit)


        public TransactionItem(com.app.banking.entity.Transaction transaction) {
            this.transactionId = transaction.getTransactionId();
            this.type = transaction.getTransactionType();
            this.amount = transaction.getAmount();
            this.date = transaction.getTransactionDate() != null ?
                    transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            this.description = transaction.getDescription();

            // Set fromAccount and toAccount directly from the transaction entity
            this.fromAccount = transaction.getSenderAccountNumber();
            this.toAccount = transaction.getReceiverAccountNumber();

            // Logic for displayBeneficiaryName (counterparty)
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) { // If amount is negative (debit)
                // This transaction is money *leaving* the customer's account.
                // The beneficiary is the receiver.
                this.displayBeneficiaryName = transaction.getReceiverAccountNumber();
                // You might fetch actual beneficiary name from a Beneficiary entity if available,
                // but for now, using the account number is a good default.
            } else { // If amount is positive (credit)
                // This transaction is money *entering* the customer's account.
                // The "beneficiary" from the *sender's* perspective is your account,
                // so the counterparty is the sender.
                this.displayBeneficiaryName = transaction.getSenderAccountNumber();
            }
        }

        // Standard getters and setters for all fields
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

        public String getToAccount() { // New getter for 'toAccount'
            return toAccount;
        }

        public void setToAccount(String toAccount) { // New setter for 'toAccount'
            this.toAccount = toAccount;
        }

        // Changed beneficiaryAccountNumber to a more generic displayBeneficiaryName
        public String getDisplayBeneficiaryName() {
            return displayBeneficiaryName;
        }

        public void setDisplayBeneficiaryName(String displayBeneficiaryName) {
            this.displayBeneficiaryName = displayBeneficiaryName;
        }

        // Removed the old get/setBeneficiaryAccountNumber and get/setBeneficiaryName
        // that were directly linked to the Transaction entity's fields.
        // The idea is to make 'displayBeneficiaryName' a consolidated field for the counterparty.
    }
}