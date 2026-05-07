package com.champsoft.vrms.event.modules.event.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventListingBranchTest {

    @Test
    void listingRejectsInvalidConstructionAndStateTransitions() {
        assertThatThrownBy(() -> new EventListing(
                EventId.newId(),
                "Jazz Night",
                "Concert",
                venue(),
                schedule(),
                0,
                0
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new EventListing(
                EventId.newId(),
                "Jazz Night",
                "Concert",
                venue(),
                schedule(),
                10,
                11
        )).isInstanceOf(IllegalArgumentException.class);

        EventListing event = event();
        event.activate();

        assertThatThrownBy(event::activate).isInstanceOf(IllegalStateException.class);

        event.cancel();
        assertThatThrownBy(event::cancel).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(event::activate).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void listingRejectsInvalidTicketAndCapacityOperations() {
        EventListing event = event();
        event.activate();

        assertThatThrownBy(() -> event.reserveTickets(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> event.reserveTickets(11)).isInstanceOf(IllegalStateException.class);

        event.reserveTickets(2);
        assertThat(event.getAvailableTickets()).isEqualTo(8);

        assertThatThrownBy(() -> event.releaseTickets(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> event.releaseTickets(5)).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> event.changeCapacity(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> event.changeCapacity(1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelledListingRejectsMutationsAndBlankUpdates() {
        EventListing event = event();
        event.cancel();

        assertThatThrownBy(() -> event.updateVenue("Bell Centre", "1909 Avenue des Canadiens", "Montreal"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> event.reschedule(schedule()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> event.updateTitle("Updated"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> event.updateDescription("Updated"))
                .isInstanceOf(IllegalStateException.class);

        EventListing activeEvent = event();
        activeEvent.activate();
        assertThatThrownBy(() -> activeEvent.updateTitle(" "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> activeEvent.updateDescription(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private EventListing event() {
        return new EventListing(
                EventId.newId(),
                "Jazz Night",
                "Concert",
                venue(),
                schedule(),
                10,
                10
        );
    }

    private Venue venue() {
        return new Venue(VenueId.newId(), "Place des Arts", "123 Main", "Montreal");
    }

    private EventSchedule schedule() {
        return new EventSchedule(LocalDateTime.of(2026, 6, 1, 19, 0), LocalDateTime.of(2026, 6, 1, 22, 0));
    }
}
