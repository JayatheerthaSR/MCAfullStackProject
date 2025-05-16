package com.app.banking.service;

import com.app.banking.entity.Beneficiary;
import com.app.banking.repository.BeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    public List<Beneficiary> findByCustomerId(Long customerId) {
        return beneficiaryRepository.findByCustomer_CustomerId(customerId);
    }

    // You can add other beneficiary-related service methods here
}