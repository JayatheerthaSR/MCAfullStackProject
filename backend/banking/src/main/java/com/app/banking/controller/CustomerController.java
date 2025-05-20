package com.app.banking.controller;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.payload.request.ExternalTransferRequest;
import com.app.banking.payload.request.InternalTransferRequest;
import com.app.banking.payload.request.TransferRequest;
import com.app.banking.payload.response.BeneficiaryResponse;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.payload.response.UserProfileResponse;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers/{customerId}") // Base path for this controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/accounts")
    public ResponseEntity<?> getCustomerAccounts(@PathVariable Long customerId) {
        List<com.app.banking.entity.Account> accounts = customerService.getCustomerAccounts(customerId);
        if (accounts != null && !accounts.isEmpty()) {
            return ResponseEntity.ok(accounts.stream()
                    .map(account -> new com.app.banking.payload.response.AccountInfoResponse(
                                account.getAccountNumber(),
                                account.getAccountType(),
                                account.getBalance()
                    ))
                    .collect(Collectors.toList()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/beneficiaries")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries(
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Customer customer = customerService.findCustomerByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        // You might want to add a check here to ensure the authenticated user
        // matches the customerId in the path for security.
        List<Beneficiary> beneficiaries = customerService.getBeneficiaries(customerId);
        List<BeneficiaryResponse> beneficiaryResponses = beneficiaries.stream()
                .map(BeneficiaryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beneficiaryResponses);
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionResponse> getTransactionsForCustomer(
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Customer customer = customerService.findCustomerByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        // You might want to add a check here to ensure the authenticated user
        // matches the customerId in the path for security.
        TransactionResponse transactionResponse = customerService.getTransactionsForCustomer(customer.getUser().getUserId());
        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long customerId, @AuthenticationPrincipal UserDetails userDetails) {
        Customer customer = customerService.findCustomerByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        // You might want to add a check here to ensure the authenticated user
        // matches the customerId in the path for security.
        UserProfileResponse profileResponse = customerService.getCustomerProfileWithAccounts(customer.getUser().getUserId());
        return ResponseEntity.ok(profileResponse);
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<?> addBeneficiary(
            @PathVariable Long customerId,
            @RequestBody Beneficiary newBeneficiary,
            @AuthenticationPrincipal UserDetails userDetails) {
        Customer customer = customerService.findCustomerByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // You should also verify here that userDetails.getUsername() is indeed authorized
        // to add a beneficiary for this customerId.

        newBeneficiary.setCustomer(customer);
        Beneficiary addedBeneficiary = customerService.addBeneficiary(customerId, newBeneficiary);
        return new ResponseEntity<>(addedBeneficiary, HttpStatus.CREATED);
    }

    // --- NEW DELETE METHOD ---
    @DeleteMapping("/beneficiaries/{beneficiaryId}") // Full path: /api/customers/{customerId}/beneficiaries/{beneficiaryId}
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long customerId,
            @PathVariable Long beneficiaryId,
            @AuthenticationPrincipal UserDetails userDetails) { // Keep for authorization checks
        try {
            // Step 1: Verify the customer exists and matches the authenticated user
            Customer customer = customerService.findCustomerByUserId(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            // Step 2: IMPORTANT SECURITY CHECK
            // Ensure the authenticated user (from userDetails) has permission to modify
            // the beneficiary for *this* customer. This is crucial to prevent
            // one customer from deleting another customer's beneficiary.
            // Example:
            if (!customer.getUser().getUsername().equals(userDetails.getUsername())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
            }

            // Step 3: Attempt to delete the beneficiary through the service
            // The service method should also verify that the beneficiary belongs to this customer.
            boolean deleted = customerService.deleteBeneficiaryOfCustomer(customerId, beneficiaryId);

            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content - success, but no content to return
            } else {
                // This could mean the beneficiary doesn't exist or doesn't belong to the customer
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer or beneficiary not found
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error deleting beneficiary: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
    // --- END NEW DELETE METHOD ---

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(
            @PathVariable Long customerId,
            @RequestBody TransferRequest transferRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if ("EXTERNAL".equalsIgnoreCase(transferRequest.getTransferType())) {
                ExternalTransferRequest externalRequest = convertToExternalTransferRequest(transferRequest);
                if (externalRequest == null) {
                    return ResponseEntity.badRequest().body("Invalid data for external transfer.");
                }
                customerService.transferExternalMoney(customerId, externalRequest);
                return ResponseEntity.ok("External transfer initiated successfully.");
            } else if ("INTERNAL".equalsIgnoreCase(transferRequest.getTransferType())) {
                InternalTransferRequest internalRequest = convertToInternalTransferRequest(transferRequest);
                if (internalRequest == null) {
                    return ResponseEntity.badRequest().body("Invalid data for internal transfer.");
                }
                customerService.transferInternalMoney(customerId, internalRequest);
                return ResponseEntity.ok("Internal transfer initiated successfully.");
            } else {
                return ResponseEntity.badRequest().body("Invalid transfer type.");
            }
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Transfer failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ExternalTransferRequest convertToExternalTransferRequest(TransferRequest transferRequest) {
        if (transferRequest != null) {
            ExternalTransferRequest externalRequest = new ExternalTransferRequest();
            externalRequest.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
            externalRequest.setAmount(transferRequest.getAmount());
            externalRequest.setDescription(transferRequest.getDescription());
            externalRequest.setTransferType(transferRequest.getTransferType());
            externalRequest.setBeneficiaryAccountNumber(transferRequest.getBeneficiaryAccountNumber());
            return externalRequest;
        }
        return null;
    }

    private InternalTransferRequest convertToInternalTransferRequest(TransferRequest transferRequest) {
        if (transferRequest != null) {
            InternalTransferRequest internalRequest = new InternalTransferRequest();
            internalRequest.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
            internalRequest.setRecipientAccountNumber(transferRequest.getRecipientAccountNumber());
            internalRequest.setAmount(transferRequest.getAmount());
            internalRequest.setDescription(transferRequest.getDescription());
            internalRequest.setTransferType(transferRequest.getTransferType());
            return internalRequest;
        }
        return null;
    }
}