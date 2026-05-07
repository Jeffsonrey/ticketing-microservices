package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.persitence;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class TicketOrderRepositoryTest {

    @Autowired
    private TicketOrderRepository repository;

    @Test
    void repositorySupportsCrudAndIdentifierQueries() {
        org.springframework.data.repository.CrudRepository<TicketOrder, Long> crudRepository = repository;
        TicketOrder saved = crudRepository.save(new TicketOrder(1L, "event-1", "VIP", 2, BigDecimal.valueOf(50), "PENDING"));

        assertThat(crudRepository.findById(saved.getId())).contains(saved);
        assertThat(repository.findByOrderIdentifier(saved.getOrderIdentifier())).contains(saved);
        assertThat(repository.existsByOrderIdentifier(saved.getOrderIdentifier())).isTrue();

        crudRepository.delete(saved);

        assertThat(crudRepository.findById(saved.getId())).isEmpty();
        assertThat(repository.findByOrderIdentifier(saved.getOrderIdentifier())).isEmpty();
        assertThat(repository.existsByOrderIdentifier(saved.getOrderIdentifier())).isFalse();
    }
}
