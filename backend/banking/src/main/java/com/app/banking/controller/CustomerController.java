package com.app.banking.controller;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Customer> customerOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Transaction> transactions = customerService.getTransactions(customerOptional.get().getAccountNumber());
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<?> addBeneficiary(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> beneficiaryData) {
        Optional<Customer> customerOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String name = beneficiaryData.get("name");
        String bank = beneficiaryData.get("bank");
        String accountNumber = beneficiaryData.get("accountNumber");
        String maxLimitStr = beneficiaryData.get("maxTransferLimit");

        if (name == null || accountNumber == null || maxLimitStr == null) {
            return new ResponseEntity<>("Name, account number, and max transfer limit are required", HttpStatus.BAD_REQUEST);
        }

        try {
            BigDecimal maxTransferLimit = new BigDecimal(maxLimitStr);
            Beneficiary beneficiary = customerService.addBeneficiary(customerOptional.get(), name, bank, accountNumber, maxTransferLimit);
            return new ResponseEntity<>(beneficiary, HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid max transfer limit format", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/beneficiaries")
    public ResponseEntity<List<Beneficiary>> getBeneficiaries(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Customer> customerOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Beneficiary> beneficiaries = customerService.getBeneficiaries(customerOptional.get().getCustomerId());
        return new ResponseEntity<>(beneficiaries, HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> transferData) {
        Optional<Customer> senderOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        if (senderOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String beneficiaryAccountNumber = transferData.get("beneficiaryAccountNumber");
        String amountStr = transferData.get("amount");

        if (beneficiaryAccountNumber == null || amountStr == null) {
            return new ResponseEntity<>("Beneficiary account number and transfer amount are required", HttpStatus.BAD_REQUEST);
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            customerService.transferMoney(senderOptional.get(), beneficiaryAccountNumber, amount);
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid transfer amount format", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Customer> customerOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        return customerOptional.map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Customer updatedCustomerData) {
        Optional<Customer> customerOptional = customerService.findByUserId(((com.app.banking.entity.User) ((org.springframework.security.core.userdetails.User) userDetails).getAuthorities().stream().findFirst().orElseThrow()).getUserId());
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Customer existingCustomer = customerOptional.get();
        // Update only the allowed fields
        existingCustomer.setFirstName(updatedCustomerData.getFirstName());
        existingCustomer.setLastName(updatedCustomerData.getLastName());
        existingCustomer.setAddress(updatedCustomerData.getAddress());
        existingCustomer.setPhoneNumber(updatedCustomerData.getPhoneNumber());
        existingCustomer.setEmailAddress(updatedCustomerData.getEmailAddress());

        Customer updatedCustomer = customerService.updateCustomerDetails(existingCustomer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }
}