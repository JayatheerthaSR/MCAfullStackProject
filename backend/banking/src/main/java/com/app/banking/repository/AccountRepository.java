package com.app.banking.repository;

import com.app.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // You can add custom query methods here if needed in the future
    // For example:
    Optional<Account> findByAccountNumber(String accountNumber); // Uncomment this line
}