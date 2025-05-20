package com.app.banking.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    @Column(name = "customer_id") // Removed @GeneratedValue
    private Long customerId;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "customer_account",
        joinColumns = { @JoinColumn(name = "customer_id") },
        inverseJoinColumns = { @JoinColumn(name = "account_number") }
    )
    private Set<Account> accounts = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "customer_id")
    private User user;

    public Customer() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods to manage the relationship
    public void addAccount(Account account) {
        this.accounts.add(account);
        account.getCustomers().add(this);
    }

    public void removeAccount(Account account) {
        this.accounts.remove(account);
        account.getCustomers().remove(this);
    }


}