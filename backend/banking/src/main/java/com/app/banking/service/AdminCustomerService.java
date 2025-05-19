package com.app.banking.service;

import com.app.banking.entity.Customer;
import com.app.banking.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminCustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Page<Customer> getAllCustomers(Pageable pageable, Specification<Customer> spec) {
        return customerRepository.findAll(spec, pageable);
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    public Optional<Customer> updateCustomer(Long customerId, Customer updatedCustomer) {
        return customerRepository.findById(customerId)
                .map(existingCustomer -> {
                    updatedCustomer.setCustomerId(customerId); // Ensure ID is consistent
                    return customerRepository.save(updatedCustomer);
                });
    }
}