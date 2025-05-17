package com.app.banking.payload.response;

import java.util.List;

import com.app.banking.entity.Role;

public class UserProfileResponse {
	private Role role;
    private Long userId; // Changed from customerId to userId for generality
    private String username; // Added username
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private List<AccountInfoResponse> accounts;

    // Constructors, Getters, Setters
    public UserProfileResponse() {
    }

    public UserProfileResponse(Long userId, String username, String firstName, String lastName, String email, String phoneNumber, String address, List<AccountInfoResponse> accounts) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.accounts = accounts;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<AccountInfoResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountInfoResponse> accounts) {
        this.accounts = accounts;
    }
}