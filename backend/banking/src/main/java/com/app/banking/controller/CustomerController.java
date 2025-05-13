package com.app.banking.controller;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam Long userId) {
        List<Transaction> transactions = customerService.getTransactionsForCustomer(userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<?> addBeneficiary(@RequestParam Long customerId, @RequestBody Beneficiary beneficiary) {
        try {
            Beneficiary addedBeneficiary = customerService.addBeneficiary(customerId, beneficiary);
            return new ResponseEntity<>(addedBeneficiary, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/beneficiaries")
    public ResponseEntity<List<Beneficiary>> getBeneficiaries(@RequestParam Long customerId) {
        List<Beneficiary> beneficiaries = customerService.getBeneficiaries(customerId);
        return ResponseEntity.ok(beneficiaries);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestParam Long customerId, @RequestBody Map<String, String> transferData) {
        try {
            customerService.transferMoney(
                    customerId,
                    transferData.get("beneficiaryAccountNumber"),
                    Double.parseDouble(transferData.get("amount")),
                    transferData.get("description")
            );
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam Long userId) {
        Optional<Customer> customerOptional = customerService.findCustomerByUserId(userId);
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>("Customer profile not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(customerOptional.get());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestParam Long userId, @RequestBody Map<String, String> updateData) {
        Optional<Customer> customerOptional = customerService.findCustomerByUserId(userId);
        if (customerOptional.isEmpty()) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        Customer existingCustomer = customerOptional.get();
        User updatedUser = new User();
        updatedUser.setFirstName(updateData.get("firstName"));
        updatedUser.setLastName(updateData.get("lastName"));
        updatedUser.setEmail(updateData.get("email"));
        updatedUser.setPhoneNumber(updateData.get("phoneNumber"));
        updatedUser.setAddress(updateData.get("address"));
        Customer updatedCustomer = customerService.updateCustomerProfile(existingCustomer, updatedUser);
        return ResponseEntity.ok(updatedCustomer);
    }
}