package com.app.banking.entity;

//import java.time.LocalDateTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Entity
//@Table(name = "LoginDetails")
//@Data
//public class LoginDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "login_id")
//    private Long loginId;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Column(name = "login_time", updatable = false)
//    private LocalDateTime loginTime = LocalDateTime.now();
//
//    @Column(name = "logout_time")
//    private LocalDateTime logoutTime;
//
//    @Column(name = "ip_address")
//    private String ipAddress;
//
//    @Column(name = "user_agent")
//    private String userAgent;
//}