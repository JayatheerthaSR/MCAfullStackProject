package com.app.banking.repository;

import com.app.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser_UserIdOrderByTransactionDateDesc(Long userId);
    List<Transaction> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<Transaction> findByUser_UserIdOrderByTransactionDateAsc(Long userId);
}