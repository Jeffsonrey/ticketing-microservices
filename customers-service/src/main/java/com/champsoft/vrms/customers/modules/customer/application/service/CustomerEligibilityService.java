package com.champsoft.vrms.customers.modules.customer.application.service;

import com.champsoft.vrms.customers.modules.customer.application.exception.CustomerNotFoundException;
import com.champsoft.vrms.customers.modules.customer.application.port.out.CustomerRepositoryPort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CustomerEligibilityService {

    private final CustomerRepositoryPort repo;

    public CustomerEligibilityService(CustomerRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligible(Long customerId) {
        return repo.findById(customerId)
                .map(customer -> true)
                .orElseThrow(() -> {
                    return new CustomerNotFoundException("Customer not found: " + customerId);
                });
    }
}