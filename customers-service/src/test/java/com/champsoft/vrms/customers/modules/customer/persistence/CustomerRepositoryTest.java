package com.champsoft.vrms.customers.modules.customer.persistence;

import com.champsoft.vrms.customers.modules.customer.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void repositorySupportsCrudAndMeaningfulQueries() {
        org.springframework.data.repository.CrudRepository<Customer, Long> crudRepository = repository;
        Customer saved = crudRepository.save(new Customer("Jane", "Doe", "jane@example.com", "5145551212"));

        assertThat(crudRepository.findById(saved.getId())).contains(saved);
        assertThat(repository.findByEmailAddress("jane@example.com")).contains(saved);
        assertThat(crudRepository.existsById(saved.getId())).isTrue();

        crudRepository.delete(saved);

        assertThat(crudRepository.findById(saved.getId())).isEmpty();
        assertThat(repository.findByEmailAddress("missing@example.com")).isEmpty();
        assertThat(crudRepository.existsById(saved.getId())).isFalse();
    }
}
