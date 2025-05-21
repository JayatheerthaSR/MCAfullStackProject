package com.app.banking.service;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import com.app.banking.entity.Role;
import com.app.banking.entity.User;
import com.app.banking.payload.request.ForgotPasswordRequest;
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Map<String, RegistrationData> pendingRegistrations = new HashMap<>();
    private final Map<Long, UpdateEmailData> pendingEmailUpdates = new HashMap<>();

    // ... (Your existing inner classes RegistrationData and UpdateEmailData)
    private static class RegistrationData {

        private User user;
        private String otp;
        private LocalDateTime expiryTime;
        private boolean emailVerified = false;

        public RegistrationData(User user, String otp, LocalDateTime expiryTime) {
            this.user = user;
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public User getUser() {
            return user;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }
    }

    private static class UpdateEmailData {

        private String newEmail;
        private String otp;
        private LocalDateTime expiryTime;
        private boolean emailVerified = false;

        public UpdateEmailData(String newEmail, String otp, LocalDateTime expiryTime) {
            this.newEmail = newEmail;
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getNewEmail() {
            return newEmail;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }
    }

    // New: Public save method for User
    public User save(User user) {
        return userRepository.save(user);
    }

    public void initiateUserRegistration(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String otp = generateOTP();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        RegistrationData registrationData = new RegistrationData(user, otp, expiryTime);
        pendingRegistrations.put(user.getEmail(), registrationData);
        // FIX: Pass the first name
        emailService.sendOTPEmail(user.getEmail(), user.getFirstName(), otp);
    }

    @Transactional
    public boolean verifyOTP(String email, String otp) {
        RegistrationData registrationData = pendingRegistrations.get(email);
        if (registrationData != null && registrationData.getOtp().equals(otp) && LocalDateTime.now().isBefore(registrationData.getExpiryTime())) {
            registrationData.setEmailVerified(true);
            User userToRegister = registrationData.getUser();

            try {
                User savedUser = userRepository.save(userToRegister);
                createCustomerForNewUser(savedUser);
                pendingRegistrations.remove(email);
                return true;
            } catch (Exception e) {
                System.err.println("Error creating customer or saving user: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void createCustomerForNewUser(User user) {
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getUserId()));

        Customer newCustomer = new Customer();
        newCustomer.setUser(managedUser);
        Customer savedCustomer = customerRepository.save(newCustomer);

        String accountNumber = generateUniqueAccountNumber();
        Account newAccount = new Account();
        newAccount.setAccountNumber(accountNumber);
        newAccount.setAccountType("SAVINGS");
        newAccount.setBalance( BigDecimal.ZERO);

        newAccount.getCustomers().add(savedCustomer);
        savedCustomer.getAccounts().add(newAccount);

        accountRepository.save(newAccount);
        customerRepository.save(savedCustomer);
    }

    private String generateUniqueAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(User existingUser, User updatedUser, boolean emailChanged) {
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhone_number(updatedUser.getPhone_number());
        existingUser.setAddress(updatedUser.getAddress());

        if (emailChanged) {
            UpdateEmailData verificationData = pendingEmailUpdates.get(existingUser.getUserId());
            if (verificationData != null && verificationData.getNewEmail().equals(updatedUser.getEmail()) && verificationData.isEmailVerified()) {
                existingUser.setEmail(updatedUser.getEmail());
                pendingEmailUpdates.remove(existingUser.getUserId()); // Clear verification data
            } else {
                throw new RuntimeException("New email address is not verified.");
            }
        }

        return userRepository.save(existingUser);
    }

    public User updateUser(User existingUser, User updatedUser) {
        return updateUser(existingUser, updatedUser, false); // Default to no email change
    }

    public List<User> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    @Transactional
    public void processForgotPasswordRequest(ForgotPasswordRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        userOptional.ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
            userRepository.save(user);

            String resetLink = "http://localhost:3000/reset-password/" + token;
            // FIX: Pass the first name
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetLink);
        });
        System.out.println("Password reset requested for username/email: " + usernameOrEmail);
    }

    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now());
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String generateOTP() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void initiateUpdateEmail(Long userId, String newEmail) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get(); // Get the user object here

        if (user.getEmail().equals(newEmail)) {
            throw new RuntimeException("New email is the same as the current email.");
        }
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("This email address is already in use.");
        }

        String otp = generateOTP();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        UpdateEmailData updateEmailData = new UpdateEmailData(newEmail, otp, expiryTime);
        pendingEmailUpdates.put(userId, updateEmailData);
        // FIX: Pass the first name
        emailService.sendUpdateEmailOTPEmail(newEmail, user.getFirstName(), otp);
    }

    public boolean verifyUpdateEmailOTP(Long userId, String newEmail, String otp) {
        UpdateEmailData updateEmailData = pendingEmailUpdates.get(userId);
        if (updateEmailData != null && updateEmailData.getNewEmail().equals(newEmail) && updateEmailData.getOtp().equals(otp) && LocalDateTime.now().isBefore(updateEmailData.getExpiryTime())) {
            updateEmailData.setEmailVerified(true);
            return true;
        }
        return false;
    }
}