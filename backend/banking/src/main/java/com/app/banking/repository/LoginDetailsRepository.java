package com.app.banking.repository;

import com.app.banking.entity.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {
    // You might add custom query methods here if needed, e.g., to find login details by user ID.
}