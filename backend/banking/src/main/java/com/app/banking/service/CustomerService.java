package com.app.banking.service;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.entity.TransactionType;
import com.app.banking.repository.BeneficiaryRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Customer createCustomer(User user, String accountNumber) {
        Customer customer = new Customer();
        customer.setUser(user); // Calls the setUser(User user) method in Customer
        customer.setAccountNumber(accountNumber); // Calls the setAccountNumber(String accountNumber) method in Customer
        return customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerByUserId(Long userId) {
        return customerRepository.findById(userId);
    }

    public Optional<Customer> findCustomerByAccountNumber(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber);
    }

    public Customer updateCustomerProfile(Customer existingCustomer, User updatedUser) {
        existingCustomer.getUser().setFirstName(updatedUser.getFirstName());
        existingCustomer.getUser().setLastName(updatedUser.getLastName());
        existingCustomer.getUser().setEmail(updatedUser.getEmail());
        existingCustomer.getUser().setPhoneNumber(updatedUser.getPhoneNumber());
        existingCustomer.getUser().setAddress(updatedUser.getAddress());
        return customerRepository.save(existingCustomer);
    }

    public Beneficiary addBeneficiary(Long customerId, Beneficiary beneficiary) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        beneficiary.setCustomer(customerOptional.get());
        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(Long customerId) {
        return beneficiaryRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional
    public void transferMoney(Long customerId, String beneficiaryAccountNumber, Double amount, String description) {
        Optional<Customer> senderOptional = customerRepository.findById(customerId);
        Optional<Customer> receiverOptional = customerRepository.findByAccountNumber(beneficiaryAccountNumber);

        if (senderOptional.isEmpty()) {
            throw new RuntimeException("Sender account not found");
        }
        if (receiverOptional.isEmpty()) {
            throw new RuntimeException("Beneficiary account not found");
        }

        Customer sender = senderOptional.get();
        Customer receiver = receiverOptional.get();

        if (sender.getAvailableBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setAvailableBalance(sender.getAvailableBalance() - amount);
        receiver.setAvailableBalance(receiver.getAvailableBalance() + amount);

        customerRepository.save(sender);
        customerRepository.save(receiver);

        Transaction transactionSender = new Transaction();
        transactionSender.setUser(sender.getUser());
        transactionSender.setTransactionType(TransactionType.TRANSFER);
        transactionSender.setAmount(-amount);
        transactionSender.setDescription("Transfer to " + beneficiaryAccountNumber + ": " + description);
        transactionSender.setBeneficiaryAccountNumber(beneficiaryAccountNumber);
        transactionRepository.save(transactionSender);

        Transaction transactionReceiver = new Transaction();
        transactionReceiver.setUser(receiver.getUser());
        transactionReceiver.setTransactionType(TransactionType.TRANSFER);
        transactionReceiver.setAmount(amount);
        transactionReceiver.setDescription("Transfer from " + sender.getAccountNumber() + ": " + description);
        transactionReceiver.setBeneficiaryAccountNumber(sender.getAccountNumber());
        transactionRepository.save(transactionReceiver);
    }

    public List<Transaction> getTransactionsForCustomer(Long userId) {
        return transactionRepository.findByUser_UserIdOrderByTransactionDateDesc(userId);
    }
}