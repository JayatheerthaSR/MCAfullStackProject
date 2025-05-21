package com.app.banking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
	List<Transaction> findByDescriptionContainingIgnoreCase(String description);

    @Query("SELECT t FROM Transaction t WHERE CAST(t.transactionId AS string) LIKE %:transactionId%")
    List<Transaction> findByTransactionIdContaining(@Param("transactionId") String transactionId);

    List<Transaction> findByTransactionId(Long transactionId);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE %:query% OR CAST(t.transactionId AS string) LIKE %:query%")
    List<Transaction> searchByDescriptionOrTransactionIdString(@Param("query") String query);
	List<Transaction> findByCustomer_CustomerIdOrderByTransactionDateDesc(Long customerId);
    List<Transaction> findByCustomer_CustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Transaction> findByCustomer_CustomerIdOrderByTransactionDateAsc(Long customerId);
}