package com.app.banking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Customers")
@Data
public class Customer {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "available_balance", nullable = false, precision = 10)
    private Double availableBalance = 0.00;

    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id")
    private User user;
}