package com.app.banking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.banking.entity.Account;
import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.TransactionType;
import com.app.banking.entity.User;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.payload.request.ExternalTransferRequest;
import com.app.banking.payload.request.InternalTransferRequest;
import com.app.banking.payload.response.AccountInfoResponse;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.payload.response.UserProfileResponse;
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.BeneficiaryRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;

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

    @Autowired
    private UserRepository userRepository;

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
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.getCustomers().add(savedCustomer);
        accountRepository.save(account);
        savedCustomer.getAccounts().add(account);

        return savedCustomer;
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUser_Username(username);
    }

    public Optional<Customer> findCustomerByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    public Optional<Customer> findCustomerByAccountNumber(String accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
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

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        if (beneficiary.getMaxTransferLimit() != null && transferAmount.compareTo(beneficiary.getMaxTransferLimit()) > 0) {
            throw new RuntimeException("Transfer amount exceeds the maximum transfer limit for this beneficiary.");
        }

        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferAmount));
        accountRepository.save(senderAccount);

        Transaction transactionSender = new Transaction();
        transactionSender.setCustomer(senderCustomer);
        transactionSender.setTransactionType(TransactionType.INTERNAL);
        transactionSender.setAmount(transferAmount.negate());
        transactionSender.setDescription("Transfer to " + beneficiary.getBeneficiaryName() + " (" + beneficiaryAccountNumber + "): " + description);
        transactionSender.setReceiverAccountNumber(beneficiaryAccountNumber);
        transactionSender.setSenderAccountNumber(sourceAccountNumber);
        transactionRepository.save(transactionSender);
    }

    public TransactionResponse getTransactionsForCustomer(Long userId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return transactionService.getTransactionsByUserId(customer);
    }

    @Transactional
    public void internalTransfer(Long senderCustomerId, String sourceAccountNumber, String recipientAccountNumber, Double amount, String description) {
        Customer senderCustomer = customerRepository.findById(senderCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender customer not found"));
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

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        if (senderAccount.getAccountNumber().equals(recipientAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferAmount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(transferAmount));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        Transaction transactionSender = new Transaction();
        transactionSender.setCustomer(senderCustomer);
        transactionSender.setTransactionType(TransactionType.INTERNAL);
        transactionSender.setAmount(transferAmount.negate());
        transactionSender.setDescription("Internal transfer to account: " + recipientAccountNumber + " - " + description);
        transactionSender.setReceiverAccountNumber(recipientAccountNumber);
        transactionSender.setSenderAccountNumber(sourceAccountNumber);
        transactionRepository.save(transactionSender);

        Transaction transactionRecipient = new Transaction();
        transactionRecipient.setCustomer(recipientCustomer);
        transactionRecipient.setTransactionType(TransactionType.INTERNAL);
        transactionRecipient.setAmount(transferAmount);
        transactionRecipient.setDescription("Internal transfer from account: " + sourceAccountNumber + " - " + description);
        transactionRecipient.setReceiverAccountNumber(recipientAccountNumber);
        transactionRecipient.setSenderAccountNumber(sourceAccountNumber);
        transactionRepository.save(transactionRecipient);
    }

    public List<Account> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            return customerRepository.findCustomerAccounts(customerId);
        }
        return null;
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public void transferExternalMoney(Long customerId, ExternalTransferRequest externalTransferRequest) {
        Customer senderCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender customer not found"));
        Account senderAccount = senderCustomer.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(externalTransferRequest.getSourceAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found for this customer"));

        if (senderAccount.getBalance().compareTo(externalTransferRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(externalTransferRequest.getAmount()));
        accountRepository.save(senderAccount);

        Transaction transactionSender = new Transaction();
        transactionSender.setCustomer(senderCustomer);
        transactionSender.setTransactionType(TransactionType.EXTERNAL);
        transactionSender.setAmount(externalTransferRequest.getAmount().negate());
        transactionSender.setDescription("External transfer to account: " + externalTransferRequest.getBeneficiaryAccountNumber() + " - " + externalTransferRequest.getDescription());
        transactionSender.setReceiverAccountNumber(externalTransferRequest.getBeneficiaryAccountNumber());
        transactionSender.setSenderAccountNumber(externalTransferRequest.getSourceAccountNumber());
        transactionRepository.save(transactionSender);
    }

    @Transactional
    public void transferInternalMoney(Long customerId, InternalTransferRequest internalTransferRequest) {
        Customer senderCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender customer not found"));
        Account senderAccount = senderCustomer.getAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(internalTransferRequest.getSourceAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found for this customer"));

        Optional<Account> recipientAccountOptional = accountRepository.findByAccountNumber(internalTransferRequest.getRecipientAccountNumber());
        Account recipientAccount = recipientAccountOptional
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        Optional<Customer> recipientCustomerOptional = recipientAccount.getCustomers().stream().findFirst();
        Customer recipientCustomer = recipientCustomerOptional
                .orElseThrow(() -> new ResourceNotFoundException("Recipient customer not found for account: " + internalTransferRequest.getRecipientAccountNumber()));

        if (senderAccount.getAccountNumber().equals(recipientAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (senderAccount.getBalance().compareTo(internalTransferRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(internalTransferRequest.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(internalTransferRequest.getAmount()));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        Transaction transactionSender = new Transaction();
        transactionSender.setCustomer(senderCustomer);
        transactionSender.setTransactionType(TransactionType.INTERNAL);
        transactionSender.setAmount(internalTransferRequest.getAmount().negate());
        transactionSender.setDescription("Internal transfer to account: " + internalTransferRequest.getRecipientAccountNumber() + " - " + internalTransferRequest.getDescription());
        transactionSender.setReceiverAccountNumber(internalTransferRequest.getRecipientAccountNumber());
        transactionSender.setSenderAccountNumber(internalTransferRequest.getSourceAccountNumber());
        transactionRepository.save(transactionSender);

        Transaction transactionRecipient = new Transaction();
        transactionRecipient.setCustomer(recipientCustomer);
        transactionRecipient.setTransactionType(TransactionType.INTERNAL);
        transactionRecipient.setAmount(internalTransferRequest.getAmount());
        transactionRecipient.setDescription("Internal transfer from account: " + senderAccount.getAccountNumber() + " - " + internalTransferRequest.getDescription());
        transactionRecipient.setReceiverAccountNumber(internalTransferRequest.getRecipientAccountNumber());
        transactionRecipient.setSenderAccountNumber(internalTransferRequest.getSourceAccountNumber());
        transactionRepository.save(transactionRecipient);
    }

    public UserProfileResponse getCustomerProfileWithAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));
        List<Account> accounts = accountRepository.findByCustomers(customer);
        List<AccountInfoResponse> accountInfoResponses = accounts.stream()
                .map(account -> new AccountInfoResponse(
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance()
                ))
                .collect(Collectors.toList());

        UserProfileResponse profileResponse = new UserProfileResponse();
        profileResponse.setUserId(user.getUserId());
        profileResponse.setUsername(user.getUsername());
        profileResponse.setRole(user.getRole());
        profileResponse.setFirstName(user.getFirstName());
        profileResponse.setLastName(user.getLastName());
        profileResponse.setEmail(user.getEmail());
        profileResponse.setPhoneNumber(user.getPhone_number());
        profileResponse.setAddress(user.getAddress());
        profileResponse.setAccounts(accountInfoResponses);

        return profileResponse;
    }

    public boolean deleteBeneficiaryOfCustomer(Long customerId, Long beneficiaryId) {
        Optional<Beneficiary> beneficiaryOptional = beneficiaryRepository.findById(beneficiaryId);

        if (beneficiaryOptional.isPresent()) {
            Beneficiary beneficiary = beneficiaryOptional.get();
            if (beneficiary.getCustomer().getCustomerId().equals(customerId)) {
                beneficiaryRepository.delete(beneficiary);
                return true;
            } else {
                throw new ResourceNotFoundException("Beneficiary not found for customer with ID: " + customerId + " or does not belong to this customer: " + beneficiaryId);
            }
        }
        return false;
    }
}