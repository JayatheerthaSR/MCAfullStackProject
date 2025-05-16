package com.app.banking.service;

import com.app.banking.entity.Account;
import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.TransactionType;
import com.app.banking.entity.User;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.payload.response.AccountInfoResponse;
import com.app.banking.payload.response.TransactionResponse; // Import TransactionResponse
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.BeneficiaryRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public List<AccountInfoResponse> getAccountsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return customer.getAccounts().stream()
                .map(account -> new AccountInfoResponse(account.getAccountNumber(), account.getAccountType(), account.getBalance()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Customer createCustomer(User user, String accountNumber) {
        Customer customer = new Customer();
        customer.setUser(user);
        Customer savedCustomer = customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType("SAVINGS"); // You can make this dynamic if needed
        account.setBalance(0.00);
        account.getCustomers().add(savedCustomer);
        accountRepository.save(account);
        savedCustomer.getAccounts().add(account);

        return savedCustomer;
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUser_Username(username);
    }

    public Optional<Customer> findCustomerByUserId(Long userId) {
        return customerRepository.findById(userId);
    }

    public Optional<Customer> findCustomerByAccountNumber(String accountNumber) {
        // Find the Account by accountNumber
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            // Since it's a ManyToMany, an account can have multiple customers.
            // Returning the first one found might not be the best approach in all scenarios.
            // You might need to adjust this based on your specific requirements.
            return accountOptional.get().getCustomers().stream().findFirst();
        }
        return Optional.empty();
    }

    public Customer updateCustomerProfile(Customer existingCustomer, User updatedUser) {
        existingCustomer.getUser().setFirstName(updatedUser.getFirstName());
        existingCustomer.getUser().setLastName(updatedUser.getLastName());
        existingCustomer.getUser().setEmail(updatedUser.getEmail());
        existingCustomer.getUser().setPhone_number(updatedUser.getPhone_number());
        existingCustomer.getUser().setAddress(updatedUser.getAddress());
        return customerRepository.save(existingCustomer);
    }

    public Beneficiary addBeneficiary(Long customerId, Beneficiary beneficiary) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (beneficiaryRepository.existsByCustomer_CustomerIdAndAccountNumber(customerId, beneficiary.getAccountNumber())) {
            throw new RuntimeException("Beneficiary with this account number already exists for this customer.");
        }

        beneficiary.setCustomer(customer);
        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(Long customerId) {
        return beneficiaryRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional
    public void transferMoney(Long customerId, String sourceAccountNumber, String beneficiaryAccountNumber, Double amount, String description) {
        Customer senderCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender customer not found"));
        Beneficiary beneficiary = beneficiaryRepository.findByAccountNumber(beneficiaryAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary account not found"));

        Account senderAccount = senderCustomer.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(sourceAccountNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found for this customer"));

        if (beneficiary.getMaxTransferLimit() != null && amount.compareTo(beneficiary.getMaxTransferLimit().doubleValue()) > 0) {
            throw new RuntimeException("Transfer amount exceeds the maximum transfer limit for this beneficiary.");
        }

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        accountRepository.save(senderAccount);

        // Record transaction for sender (External)
        Transaction transactionSender = new Transaction();
        transactionSender.setUser(senderCustomer.getUser());
        transactionSender.setTransactionType(TransactionType.TRANSFER); // Use TRANSFER for external
        transactionSender.setAmount(-amount);
        transactionSender.setDescription("External transfer to " + beneficiary.getBeneficiaryName() + " (" + beneficiaryAccountNumber + "): " + description);
        transactionSender.setBeneficiaryAccountNumber(beneficiaryAccountNumber);
        transactionRepository.save(transactionSender);

        // Placeholder for external payment gateway interaction:
        // System.out.println("Initiating external transfer to: " + beneficiary.getBankName() + " - " + beneficiaryAccountNumber + " for amount: " + amount);
    }

    public List<TransactionResponse> getTransactionsForCustomer(Long userId) { // Changed return type to List<TransactionResponse>
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return transactionService.getTransactionsByUserId(customer.getUser().getUserId()); // This method in TransactionService should return List<TransactionResponse>
    }

    @Transactional
    public void internalTransfer(Long senderCustomerId, String sourceAccountNumber, String recipientAccountNumber, Double amount, String description) {
        Customer senderCustomer = customerRepository.findById(senderCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender customer not found"));
        // Need to find the recipient Customer based on the recipientAccountNumber
        Optional<Account> recipientAccountOptionalForCustomer = accountRepository.findByAccountNumber(recipientAccountNumber);
        Customer recipientCustomer = recipientAccountOptionalForCustomer.flatMap(account -> account.getCustomers().stream().findFirst())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient customer not found for account number: " + recipientAccountNumber));

        Account senderAccount = senderCustomer.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(sourceAccountNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found for this customer"));

        Account recipientAccount = recipientCustomer.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(recipientAccountNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        if (senderAccount.getAccountNumber().equals(recipientAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        // Record transaction for sender (Internal)
        Transaction transactionSender = new Transaction();
        transactionSender.setUser(senderCustomer.getUser());
        transactionSender.setTransactionType(TransactionType.INTERNAL_TRANSFER);
        transactionSender.setAmount(-amount);
        transactionSender.setDescription("Internal transfer to account: " + recipientAccountNumber + " - " + description);
        transactionSender.setBeneficiaryAccountNumber(recipientAccountNumber);
        transactionRepository.save(transactionSender);

        // Record transaction for recipient (Internal)
        Transaction transactionRecipient = new Transaction();
        transactionRecipient.setUser(recipientCustomer.getUser());
        transactionRecipient.setTransactionType(TransactionType.INTERNAL_TRANSFER);
        transactionRecipient.setAmount(amount);
        transactionRecipient.setDescription("Internal transfer from account: " + senderAccount.getAccountNumber() + " - " + description);
        transactionRecipient.setBeneficiaryAccountNumber(senderAccount.getAccountNumber());
        transactionRepository.save(transactionRecipient);
    }

    public List<Account> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            return customerRepository.findCustomerAccounts(customerId);
        }
        return null;
    }

    public Optional<Customer> findById(Long id) { // Add this method
        return customerRepository.findById(id);
    }
}