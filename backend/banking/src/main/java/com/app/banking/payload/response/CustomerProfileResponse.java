package com.app.banking.payload.response;

import java.util.List;

public class CustomerProfileResponse {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private List<AccountInfoResponse> accounts;

    // Constructors, Getters, Setters
    public CustomerProfileResponse() {
    }

    public CustomerProfileResponse(Long customerId, String firstName, String lastName, String email, String phoneNumber, String address, List<AccountInfoResponse> accounts) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.accounts = accounts;
    }

    // Getters and Setters (as previously defined)
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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