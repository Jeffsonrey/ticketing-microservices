package com.champsoft.vrms.ticketinventory.modules.ticketInventory.persistence;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class TicketInventoryRepositoryTest {

    @Autowired
    private TicketInventoryRepository repository;

    @Test
    void repositorySupportsCrudAndMeaningfulLookup() {
        org.springframework.data.repository.CrudRepository<TicketInventory, Long> crudRepository = repository;
        TicketInventory saved = crudRepository.save(new TicketInventory("event-abc", "VIP", 100, 50));

        assertThat(crudRepository.findById(saved.getId())).contains(saved);
        assertThat(repository.findByEventIdAndTicketType("event-abc", "VIP")).contains(saved);
        assertThat(crudRepository.existsById(saved.getId())).isTrue();

        crudRepository.delete(saved);

        assertThat(crudRepository.findById(saved.getId())).isEmpty();
        assertThat(repository.findByEventIdAndTicketType("missing", "VIP")).isEmpty();
        assertThat(crudRepository.existsById(saved.getId())).isFalse();
    }
}
