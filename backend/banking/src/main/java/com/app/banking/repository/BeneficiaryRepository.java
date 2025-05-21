package com.app.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.Beneficiary;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
	List<Beneficiary> findByBeneficiaryNameContainingIgnoreCaseOrAccountNumberContaining(String beneficiaryName, String accountNumber);
    List<Beneficiary> findByCustomer_CustomerId(Long customerId);
    boolean existsByCustomer_CustomerIdAndAccountNumber(Long customerId, String accountNumber);
    Optional<Beneficiary> findByAccountNumber(String accountNumber);
}