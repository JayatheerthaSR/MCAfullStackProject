package com.app.banking.repository;

import com.app.banking.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Import Optional

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByCustomer_CustomerId(Long customerId);
    boolean existsByCustomer_CustomerIdAndAccountNumber(Long customerId, String accountNumber);

    // Add this method:
    Optional<Beneficiary> findByAccountNumber(String accountNumber);
}