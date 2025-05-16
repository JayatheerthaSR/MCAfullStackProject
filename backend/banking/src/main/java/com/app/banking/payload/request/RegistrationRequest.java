package com.app.banking.payload.request;

import jakarta.validation.constraints.*;

public class RegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String phone;

    @NotBlank
    @Size(min = 8, max = 100) // Enforce minimum length of 8
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-`~\\\\\\[\\]\\{\\}\\|;':\",./<>?]).*$",
        message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    private String firstName;
    private String lastName;
    private String address;
    private String role;

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username (if needed)
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password (if needed)
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for firstName
    public String getFirstName() {
        return firstName;
    }

    // Setter for firstName (if needed)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter for lastName
    public String getLastName() {
        return lastName;
    }

    // Setter for lastName (if needed)
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email (if needed)
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for address
    public String getAddress() {
        return address;
    }

    // Setter for address (if needed)
    public void setAddress(String address) {
        this.address = address;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role (if needed)
    public void setRole(String role) {
        this.role = role;
    }

    // Getter for phone
    public String getPhone() {
        return phone;
    }

    // Setter for phone (if needed)
    public void setPhone(String phone) {
        this.phone = phone;
    }
}