package com.champsoft.vrms.customers.modules.customer.application.service;

import com.champsoft.vrms.customers.modules.customer.application.exception.CustomerNotFoundException;
import com.champsoft.vrms.customers.modules.customer.application.port.out.CustomerRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CustomerEligibilityService {
    private final CustomerRepositoryPort repo;

    public CustomerEligibilityService(CustomerRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public boolean isEligible(Strong customerId) {
        return repo.findById(CustomerId.ofcustomerId))
                .map(c -> c.isEligibleForRegistration())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found " +)
    }
}
