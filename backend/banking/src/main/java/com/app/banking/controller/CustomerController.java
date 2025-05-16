package com.app.banking.controller;

import com.app.banking.payload.request.InternalTransferRequest;
import com.app.banking.payload.request.ExternalTransferRequest;
import com.app.banking.payload.response.AccountInfoResponse;
import com.app.banking.payload.response.BeneficiaryResponse;
import com.app.banking.payload.response.CustomerProfileResponse;
import com.app.banking.entity.Account;
import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.service.BeneficiaryService;
import com.app.banking.service.CustomerService;
import com.app.banking.service.TransactionService;
import com.app.banking.service.UserService;
import com.app.banking.payload.response.TransactionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers/{customerId}")
@PreAuthorize("hasRole('CUSTOMER')") // Ensure only customers can access these endpoints
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private BeneficiaryService beneficiaryService;
    
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountInfoResponse>> getCustomerAccounts(@PathVariable Long customerId) {
        List<AccountInfoResponse> accountInfoResponses = customerService.getAccountsByCustomerId(customerId); // Calling the correct method

        if (accountInfoResponses != null && !accountInfoResponses.isEmpty()) {
            return ResponseEntity.ok(accountInfoResponses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long customerId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<User> loggedInUserOptional = userService.findByUsername(username);

        if (loggedInUserOptional.isEmpty() || !loggedInUserOptional.get().getUserId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Handle unauthorized access
        }

        List<TransactionResponse> transactionResponses = transactionService.getTransactionsByUserId(customerId); // Use the service method that returns TransactionResponse
        return ResponseEntity.ok(transactionResponses);
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<?> addBeneficiary(
            @PathVariable Long customerId,
            @RequestBody Beneficiary beneficiary,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        Optional<Customer> loggedInCustomerOptional = customerService.findByUsername(username);

        if (loggedInCustomerOptional.isEmpty() || !loggedInCustomerOptional.get().getCustomerId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Customer loggedInCustomer = loggedInCustomerOptional.get();

        try {
            Beneficiary addedBeneficiary = customerService.addBeneficiary(customerId, beneficiary);
            return new ResponseEntity<>(addedBeneficiary, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Beneficiary with this account number already exists")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            } else if (e.getMessage().equals("Customer not found")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/beneficiaries")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Optional<Customer> customerOptional = customerService.findByUsername(username);
            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                List<Beneficiary> beneficiaries = beneficiaryService.findByCustomerId(customer.getCustomerId());
                List<BeneficiaryResponse> beneficiaryResponses = beneficiaries.stream()
                        .map(BeneficiaryResponse::new)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(beneficiaryResponses, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@PathVariable Long customerId, @RequestBody ExternalTransferRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Customer> loggedInCustomerOptional = customerService.findByUsername(username);

        if (loggedInCustomerOptional.isEmpty() || !loggedInCustomerOptional.get().getCustomerId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Customer loggedInCustomer = loggedInCustomerOptional.get();

        try {
            customerService.transferMoney(
                    customerId,
                    request.getSourceAccountNumber(),
                    request.getBeneficiaryAccountNumber(),
                    request.getAmount(),
                    request.getDescription()
            );
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Insufficient balance")) {
                return new ResponseEntity<>("Insufficient Balance", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transfer/internal")
    public ResponseEntity<?> internalTransfer(@PathVariable Long customerId, @RequestBody InternalTransferRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Customer> loggedInCustomerOptional = customerService.findByUsername(username);

        if (loggedInCustomerOptional.isEmpty() || !loggedInCustomerOptional.get().getCustomerId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Customer loggedInCustomer = loggedInCustomerOptional.get();

        try {
            customerService.internalTransfer(
                    customerId,
                    request.getSourceAccountNumber(),
                    request.getRecipientAccountNumber(),
                    request.getAmount(),
                    request.getDescription()
            );
            return new ResponseEntity<>("Internal transfer successful", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Insufficient balance")) {
                return new ResponseEntity<>("Insufficient Balance", HttpStatus.BAD_REQUEST);
            } else if (e.getMessage().equals("Recipient account not found")) {
                return new ResponseEntity<>("Recipient account not found", HttpStatus.BAD_REQUEST);
            } else if (e.getMessage().equals("Cannot transfer to the same account")) {
                return new ResponseEntity<>("Cannot transfer to the same account", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long customerId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Customer> loggedInCustomerOptional = customerService.findByUsername(username);

        if (loggedInCustomerOptional.isEmpty() || !loggedInCustomerOptional.get().getCustomerId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>("Customer profile not found", HttpStatus.NOT_FOUND);
        }
        Customer customer = customerOptional.get();

        List<AccountInfoResponse> accountResponses = customer.getAccounts().stream()
        		.map(account -> new AccountInfoResponse(
        				account.getAccountNumber(), 
        				account.getAccountType(), 
        				account.getBalance()))
                .collect(Collectors.toList());

        CustomerProfileResponse profileResponse = new CustomerProfileResponse(
                customer.getCustomerId(),
                customer.getUser().getFirstName(),
                customer.getUser().getLastName(),
                customer.getUser().getEmail(),
                customer.getUser().getPhone_number(), // Assuming this field exists in your User entity
                customer.getUser().getAddress(),       // Assuming this field exists in your User entity
                accountResponses
        );

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long customerId, @RequestBody Map<String, String> updateData, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Long loggedInUserId = Long.parseLong(userDetails.getUsername()); // Assuming username is the userId
        if (!loggedInUserId.equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Customer> customerOptional = customerService.findCustomerByUserId(loggedInUserId);
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        Customer existingCustomer = customerOptional.get();
        User updatedUser = new User();
        updatedUser.setFirstName(updateData.get("firstName"));
        updatedUser.setLastName(updateData.get("lastName"));
        updatedUser.setEmail(updateData.get("email"));
        updatedUser.setPhone_number(updateData.get("phoneNumber"));
        updatedUser.setAddress(updateData.get("address"));
        Customer updatedCustomer = customerService.updateCustomerProfile(existingCustomer, updatedUser);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getCustomerBalance(@PathVariable Long customerId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Long loggedInUserId = Long.parseLong(userDetails.getUsername()); // Assuming username is the userId
        if (!loggedInUserId.equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Customer> customerOptional = customerService.findCustomerByUserId(loggedInUserId);
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        Customer customer = customerOptional.get();

        // Assuming a customer can have multiple accounts, you might want to:
        // 1. Return balances of all accounts.
        // 2. Allow the user to specify an account number to get the balance for.
        // For now, let's return the balance of the first account (you might need a more specific logic)
        if (!customer.getAccounts().isEmpty()) {
            Account firstAccount = customer.getAccounts().iterator().next(); // Get the first account
            return ResponseEntity.ok(firstAccount.getBalance());
        } else {
            return ResponseEntity.ok(0.0); // Or handle the case where the customer has no accounts
        }
    }
}