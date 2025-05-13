package com.app.banking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import io.jsonwebtoken.SignatureAlgorithm;



@Entity
@Table(name = "Users")
@Data

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	public CharSequence getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getLastName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getPhoneNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFirstName(String username2) {
		// TODO Auto-generated method stub
		
	}

	public void setLastName(Object lastName2) {
		// TODO Auto-generated method stub
		
	}

	public String getFirstName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEmail(String email2) {
		// TODO Auto-generated method stub
		
	}

	public void setPhoneNumber(Object phoneNumber2) {
		// TODO Auto-generated method stub
		
	}

	public void setPassword(String encode) {
		// TODO Auto-generated method stub
		
	}

	public Role getRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUserId() {
		// TODO Auto-generated method stub
		return null;
	}



}