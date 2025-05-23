package com.app.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.Account;
import com.app.banking.entity.Customer;
import com.app.banking.entity.User;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByUser_Username(String username);

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByUser_UserId(Long userId);

    @Query("SELECT a.accountNumber, a.accountType FROM Customer c JOIN c.accounts a WHERE c.user.userId = :customerId")
    List<Object[]> findAccountsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT a FROM Customer c JOIN c.accounts a WHERE c.user.userId = :customerId")
    List<Account> findCustomerAccounts(@Param("customerId") Long customerId);
}