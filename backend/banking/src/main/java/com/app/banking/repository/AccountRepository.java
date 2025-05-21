package com.app.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
	List<Account> findByAccountNumberContaining(String accountNumber);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomers(Customer customer);
}