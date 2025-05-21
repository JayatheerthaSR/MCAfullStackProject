package com.app.banking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.entity.Beneficiary;
import com.app.banking.repository.BeneficiaryRepository;

@Service
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    public List<Beneficiary> findByCustomerId(Long customerId) {
        return beneficiaryRepository.findByCustomer_CustomerId(customerId);
    }

}