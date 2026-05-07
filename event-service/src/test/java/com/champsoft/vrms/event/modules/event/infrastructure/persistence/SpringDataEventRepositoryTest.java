package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class SpringDataEventRepositoryTest {

    @Autowired
    private SpringDataEventRepository repository;

    @Test
    void repositorySupportsCrudAndDuplicateLookup() {
        EventJpaEntity entity = new EventJpaEntity();
        entity.id = "event-test-123";
        entity.title = "Rock Show";
        entity.description = "Live performance";
        entity.venue = new VenueEmbeddable("venue-test", "Bell Centre", "1909 Ave", "Montreal");
        entity.schedule = new EventScheduleEmbeddable(
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0)
        );
        entity.totalCapacity = 500;
        entity.availableTickets = 500;
        entity.status = "ACTIVE";

        repository.save(entity);

        assertThat(repository.findById("event-test-123")).isPresent();
        assertThat(repository.findByTitleAndVenue_NameAndSchedule_StartDateTimeAndSchedule_EndDateTime(
                "Rock Show",
                "Bell Centre",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0)
        )).isPresent();
        assertThat(repository.existsById("event-test-123")).isTrue();

        repository.deleteById("event-test-123");

        assertThat(repository.findById("event-test-123")).isEmpty();
        assertThat(repository.findByTitleAndVenue_NameAndSchedule_StartDateTimeAndSchedule_EndDateTime(
                "Missing",
                "Nowhere",
                LocalDateTime.of(2026, 7, 1, 19, 0),
                LocalDateTime.of(2026, 7, 1, 22, 0)
        )).isEmpty();
    }
}
