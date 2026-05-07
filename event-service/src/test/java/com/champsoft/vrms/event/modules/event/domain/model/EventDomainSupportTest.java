package com.champsoft.vrms.event.modules.event.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventDomainSupportTest {

    @Test
    void eventIdAndVenueIdSupportFactoriesEqualityAndStringConversion() {
        EventId eventId = EventId.of("event-123");
        VenueId venueId = VenueId.of("venue-123");

        assertThat(EventId.newId().value()).isNotBlank();
        assertThat(VenueId.newId().value()).isNotBlank();
        assertThat(eventId).isEqualTo(EventId.of("event-123"));
        assertThat(eventId.hashCode()).isEqualTo(EventId.of("event-123").hashCode());
        assertThat(eventId.toString()).isEqualTo("event-123");
        assertThat(venueId).isEqualTo(VenueId.of("venue-123"));
        assertThat(venueId.hashCode()).isEqualTo(VenueId.of("venue-123").hashCode());
        assertThat(venueId.toString()).isEqualTo("venue-123");
    }

    @Test
    void venueRejectsBlankFieldsAndAllowsUpdates() {
        Venue venue = new Venue(VenueId.of("venue-1"), "Place des Arts", "123 Main", "Montreal");

        venue.updateDetails("Bell Centre", "1909 Avenue des Canadiens", "Montreal");

        assertThat(venue.getName()).isEqualTo("Bell Centre");
        assertThat(venue.getAddress()).isEqualTo("1909 Avenue des Canadiens");

        assertThatThrownBy(() -> new Venue(VenueId.of("venue-2"), "", "123 Main", "Montreal"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> venue.updateDetails(" ", "1909 Avenue des Canadiens", "Montreal"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eventScheduleRejectsInvalidOrNullDates() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 19, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 22, 0);
        EventSchedule schedule = new EventSchedule(start, end);

        assertThat(schedule.startDateTime()).isEqualTo(start);
        assertThat(schedule.endDateTime()).isEqualTo(end);

        assertThatThrownBy(() -> new EventSchedule(start, start))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new EventSchedule(null, end))
                .isInstanceOf(NullPointerException.class);
    }
}
