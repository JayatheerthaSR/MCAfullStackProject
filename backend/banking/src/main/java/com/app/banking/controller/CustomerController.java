package com.app.banking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.Beneficiary;
import com.app.banking.entity.Customer;
import com.app.banking.exception.ResourceNotFoundException;
import com.app.banking.payload.request.ExternalTransferRequest;
import com.app.banking.payload.request.InternalTransferRequest;
import com.app.banking.payload.request.TransferRequest;
import com.app.banking.payload.response.BeneficiaryResponse;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.payload.response.UserProfileResponse;
import com.app.banking.service.CustomerService;

@RestController
@RequestMapping("/api/customers/{customerId}")
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
//        Customer customer = customerService.findCustomerByUserId(customerId)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
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
        TransactionResponse transactionResponse = customerService.getTransactionsForCustomer(customer.getUser().getUserId());
        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long customerId, @AuthenticationPrincipal UserDetails userDetails) {
        Customer customer = customerService.findCustomerByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
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

        newBeneficiary.setCustomer(customer);
        Beneficiary addedBeneficiary = customerService.addBeneficiary(customerId, newBeneficiary);
        return new ResponseEntity<>(addedBeneficiary, HttpStatus.CREATED);
    }

    @DeleteMapping("/beneficiaries/{beneficiaryId}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long customerId,
            @PathVariable Long beneficiaryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.findCustomerByUserId(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            if (!customer.getUser().getUsername().equals(userDetails.getUsername())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            boolean deleted = customerService.deleteBeneficiaryOfCustomer(customerId, beneficiaryId);

            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error deleting beneficiary: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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