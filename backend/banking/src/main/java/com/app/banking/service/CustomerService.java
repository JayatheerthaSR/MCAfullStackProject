package com.app.banking.service;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.repository.BeneficiaryRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Customer createCustomer(User user, String firstName, String lastName, String emailAddress, String phoneNumber, String address) {
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmailAddress(emailAddress);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        customer.setAccountNumber(generateAccountNumber());
        return customerRepository.save(customer);
    }

    public Optional<Customer> findByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    public Optional<Customer> findByAccountNumber(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber);
    }

    public Customer updateCustomerDetails(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber);
    }

    public Beneficiary addBeneficiary(Customer customer, String name, String bank, String accountNumber, BigDecimal maxLimit) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setCustomer(customer);
        beneficiary.setBeneficiaryName(name);
        beneficiary.setBankName(bank);
        beneficiary.setAccountNumber(accountNumber);
        beneficiary.setMaxTransferLimit(maxLimit);
        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(Long customerId) {
        return beneficiaryRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional
    public void transferMoney(Customer sender, String beneficiaryAccountNumber, BigDecimal amount) {
        if (sender.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        Optional<Customer> receiverOptional = customerRepository.findByAccountNumber(beneficiaryAccountNumber);
        if (receiverOptional.isEmpty()) {
            throw new RuntimeException("Beneficiary account not found");
        }
        Customer receiver = receiverOptional.get();

        // Debit from sender
        sender.setAvailableBalance(sender.getAvailableBalance().subtract(amount));
        customerRepository.save(sender);

        // Credit to receiver
        receiver.setAvailableBalance(receiver.getAvailableBalance().add(amount));
        customerRepository.save(receiver);

        // Record transaction for sender
        Transaction senderTransaction = new Transaction();
        senderTransaction.setAccountNumber(sender.getAccountNumber());
        senderTransaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        senderTransaction.setAmount(amount.negate()); // Debit is negative
        senderTransaction.setDescription("Transfer to " + receiver.getFirstName() + " (" + beneficiaryAccountNumber + ")");
        senderTransaction.setBeneficiaryAccountNumber(beneficiaryAccountNumber);
        transactionRepository.save(senderTransaction);

        // Record transaction for receiver
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setAccountNumber(receiver.getAccountNumber());
        receiverTransaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        receiverTransaction.setAmount(amount); // Credit is positive
        receiverTransaction.setDescription("Transfer from " + sender.getFirstName() + " (" + sender.getAccountNumber() + ")");
        receiverTransaction.setBeneficiaryAccountNumber(sender.getAccountNumber());
        transactionRepository.save(receiverTransaction);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16); // Simple account number generation
    }
}