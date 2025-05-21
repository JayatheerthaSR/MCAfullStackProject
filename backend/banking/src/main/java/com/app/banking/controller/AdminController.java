package com.app.banking.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.entity.User;
import com.app.banking.payload.response.AdminProfileResponse;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.service.TransactionService;
import com.app.banking.service.UserService;

@RestController
@RequestMapping("/api/admins/{adminId}")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(@PathVariable Long adminId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> adminUserOptional = userService.findById(adminId);
        if (adminUserOptional.isEmpty()) {
            return new ResponseEntity<>("Admin profile not found", HttpStatus.NOT_FOUND);
        }
        User adminUser = adminUserOptional.get();

        AdminProfileResponse profileResponse = new AdminProfileResponse(
                adminId,
                adminUser.getFirstName(),
                adminUser.getLastName(),
                adminUser.getEmail(),
                adminUser.getPhone_number(),
                adminUser.getAddress()
        );

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateAdminProfile(@PathVariable Long adminId, @RequestBody Map<String, String> updateData, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> adminUserOptional = userService.findById(adminId);
        if (adminUserOptional.isEmpty()) {
            return new ResponseEntity<>("Admin not found", HttpStatus.NOT_FOUND);
        }
        User existingAdminUser = adminUserOptional.get();

        if (updateData.containsKey("firstName")) {
            existingAdminUser.setFirstName(updateData.get("firstName"));
        }
        if (updateData.containsKey("lastName")) {
            existingAdminUser.setLastName(updateData.get("lastName"));
        }
        if (updateData.containsKey("email")) {
            existingAdminUser.setEmail(updateData.get("email"));
        }
        if (updateData.containsKey("phoneNumber")) {
            existingAdminUser.setPhone_number(updateData.get("phoneNumber"));
        }
        if (updateData.containsKey("address")) {
            existingAdminUser.setAddress(updateData.get("address"));
        }

        userService.save(existingAdminUser);

        return ResponseEntity.ok("Admin profile updated successfully");
    }

    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
        Optional<User> customer = userService.findById(customerId);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionResponse> getAllTransactions(@PathVariable Long adminId) {
        TransactionResponse allTransactionsResponse = transactionService.getAllTransactions();

        return new ResponseEntity<>(allTransactionsResponse, HttpStatus.OK);
    }
}