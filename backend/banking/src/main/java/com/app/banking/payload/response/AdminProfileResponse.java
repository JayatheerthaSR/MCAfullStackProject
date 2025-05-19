package com.app.banking.payload.response;

public class AdminProfileResponse {
    private Long adminId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    public AdminProfileResponse(Long adminId, String firstName, String lastName, String email, String phoneNumber, String address) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters for all fields

    public Long getAdminId() {
        return adminId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    // Optional: Setters if you need to modify the response object later
}