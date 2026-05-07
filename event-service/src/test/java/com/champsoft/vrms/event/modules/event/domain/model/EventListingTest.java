package com.champsoft.vrms.event.modules.event.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventListingTest {

    @Test
    void eventActivationAndEligibilityBehaveAsExpected() {
        EventListing event = event();

        assertThat(event.isEligibleForTicketing()).isFalse();

        event.activate();

        assertThat(event.isEligibleForTicketing()).isTrue();
    }

    @Test
    void reserveTicketsAndCapacityRulesAreEnforced() {
        EventListing event = event();
        event.activate();
        event.reserveTickets(2);

        assertThat(event.getAvailableTickets()).isEqualTo(8);

        assertThatThrownBy(() -> event.changeCapacity(1))
                .isInstanceOf(IllegalStateException.class);
    }

    private EventListing event() {
        return new EventListing(
                EventId.newId(),
                "Jazz Night",
                "Concert",
                new Venue(VenueId.newId(), "Place des Arts", "123 Main", "Montreal"),
                new EventSchedule(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2)),
                10,
                10
        );
    }
}
