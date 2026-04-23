package com.champsoft.vrms.customers.modules.customer.persistence;

import com.champsoft.vrms.customers.modules.customer.application.port.out.CustomerRepositoryPort;
import com.champsoft.vrms.customers.modules.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryPort {

    Optional<Customer> findByEmailAddress(String emailAddress);
}