package com.app.banking.service;

import com.app.banking.entity.User;
import com.app.banking.entity.Role;
import com.app.banking.payload.request.ForgotPasswordRequest;
import com.app.banking.repository.UserRepository;
import com.app.banking.repository.AccountRepository;
import com.app.banking.repository.CustomerRepository;
import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
        emailService.sendOTPEmail(user.getEmail(), otp);
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
        newAccount.setBalance(0.0);

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

    public User updateUser(User existingUser, User updatedUser) {
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone_number(updatedUser.getPhone_number());
        existingUser.setAddress(updatedUser.getAddress());
        return userRepository.save(existingUser);
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
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
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
}