package com.app.banking.controller;

import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.payload.response.AdminProfileResponse;
import com.app.banking.payload.response.TransactionResponse;
import com.app.banking.service.AdminService; // Assuming you have an AdminService
import com.app.banking.service.TransactionService;
import com.app.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins/{adminId}")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AdminService adminService; // Autowire the AdminService

    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(@PathVariable Long adminId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> adminUserOptional = userService.findById(adminId); // Assuming Admin entity extends/contains User details
        if (adminUserOptional.isEmpty()) {
            return new ResponseEntity<>("Admin profile not found", HttpStatus.NOT_FOUND);
        }
        User adminUser = adminUserOptional.get();

        AdminProfileResponse profileResponse = new AdminProfileResponse( // Adapt to your AdminProfileResponse
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

        // Update the User entity
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

        userService.save(existingAdminUser); // Assuming UserService has a save method

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
        List<TransactionResponse> allTransactions = transactionService.getAllTransactions();
        // Now you can work with the list of TransactionResponse objects
        // You might need to adjust the subsequent mapping logic accordingly
        TransactionResponse response = new TransactionResponse(allTransactions.stream()
                .flatMap(tr -> tr.getTransactions().stream())
                .collect(Collectors.toList()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}