package com.champsoft.vrms.event.modules.event.application.service;

import com.champsoft.vrms.event.modules.event.application.exception.DuplicateEventException;
import com.champsoft.vrms.event.modules.event.application.exception.EventNotFoundException;
import com.champsoft.vrms.event.modules.event.application.port.out.EventRepositoryPort;
import com.champsoft.vrms.event.modules.event.domain.model.EventId;
import com.champsoft.vrms.event.modules.event.domain.model.EventListing;
import com.champsoft.vrms.event.modules.event.domain.model.EventSchedule;
import com.champsoft.vrms.event.modules.event.domain.model.Venue;
import com.champsoft.vrms.event.modules.event.domain.model.VenueId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepositoryPort repository;

    @InjectMocks
    private EventCrudService crudService;

    @Test
    void createEventSavesWhenNotDuplicate() {
        when(repository.save(any(EventListing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventListing saved = crudService.create(
                "Jazz Night",
                "Concert",
                "Place des Arts",
                "123 Main",
                "Montreal",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0),
                250
        );

        assertThat(saved.getTitle()).isEqualTo("Jazz Night");
        verify(repository).findDuplicate("Jazz Night", "Place des Arts",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0));
    }

    @Test
    void createEventRejectsDuplicates() {
        when(repository.findDuplicate(any(), any(), any(), any())).thenReturn(Optional.of(event("event-1")));

        assertThatThrownBy(() -> crudService.create(
                "Jazz Night",
                "Concert",
                "Place des Arts",
                "123 Main",
                "Montreal",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0),
                250
        )).isInstanceOf(DuplicateEventException.class);
    }

    @Test
    void getUpdateAndEligibilityBehaveAsExpected() {
        EventListing active = event("event-1");
        active.activate();
        EventEligibilityService eligibilityService = new EventEligibilityService(repository);

        when(repository.findById(EventId.of("event-1"))).thenReturn(Optional.of(active));
        when(repository.findById(EventId.of("missing"))).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of(active));

        assertThat(crudService.list()).containsExactly(active);
        assertThat(crudService.getById("event-1")).isSameAs(active);
        assertThat(eligibilityService.isEligible("event-1")).isTrue();
        assertThatThrownBy(() -> crudService.getById("missing")).isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void updateEventAppliesAllMutableFields() {
        EventListing existing = event("event-1");
        when(repository.findById(EventId.of("event-1"))).thenReturn(Optional.of(existing));
        when(repository.findDuplicate(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(EventListing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventListing updated = crudService.update(
                "event-1",
                "Updated Title",
                "Updated Description",
                "Bell Centre",
                "1909 Avenue des Canadiens",
                "Montreal",
                LocalDateTime.of(2026, 6, 2, 19, 0),
                LocalDateTime.of(2026, 6, 2, 22, 0),
                150
        );

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getVenue().getName()).isEqualTo("Bell Centre");
        assertThat(updated.getSchedule().startDateTime()).isEqualTo(LocalDateTime.of(2026, 6, 2, 19, 0));
        assertThat(updated.getTotalCapacity()).isEqualTo(150);
    }

    @Test
    void updateEventUsesCurrentValuesWhenOptionalFieldsAreNull() {
        EventListing existing = event("event-1");
        when(repository.findById(EventId.of("event-1"))).thenReturn(Optional.of(existing));
        when(repository.findDuplicate("Jazz Night", "Place des Arts",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0))).thenReturn(Optional.of(existing));
        when(repository.save(any(EventListing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventListing updated = crudService.update(
                "event-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(updated.getTitle()).isEqualTo("Jazz Night");
        assertThat(updated.getVenue().getName()).isEqualTo("Place des Arts");
    }

    @Test
    void updateEventRejectsDuplicateFromDifferentEvent() {
        EventListing existing = event("event-1");
        EventListing duplicate = event("event-2");
        when(repository.findById(EventId.of("event-1"))).thenReturn(Optional.of(existing));
        when(repository.findDuplicate("Jazz Night", "Place des Arts",
                LocalDateTime.of(2026, 6, 1, 19, 0),
                LocalDateTime.of(2026, 6, 1, 22, 0))).thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> crudService.update(
                "event-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        )).isInstanceOf(DuplicateEventException.class);
    }

    @Test
    void activateCancelAndDeleteUseRepositoryAndMissingDeleteThrows() {
        EventListing existing = event("event-1");
        when(repository.findById(EventId.of("event-1"))).thenReturn(Optional.of(existing));
        when(repository.findById(EventId.of("missing"))).thenReturn(Optional.empty());
        when(repository.save(any(EventListing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventListing activated = crudService.activate("event-1");
        assertThat(activated.getStatus().name()).isEqualTo("ACTIVE");

        EventListing cancelled = crudService.cancel("event-1");
        assertThat(cancelled.getStatus().name()).isEqualTo("CANCELLED");

        crudService.delete("event-1");
        verify(repository).deleteById(EventId.of("event-1"));

        assertThatThrownBy(() -> crudService.delete("missing"))
                .isInstanceOf(EventNotFoundException.class);
    }

    private EventListing event(String id) {
        return new EventListing(
                EventId.of(id),
                "Jazz Night",
                "Concert",
                new Venue(VenueId.newId(), "Place des Arts", "123 Main", "Montreal"),
                new EventSchedule(LocalDateTime.of(2026, 6, 1, 19, 0), LocalDateTime.of(2026, 6, 1, 22, 0)),
                100,
                100
        );
    }
}
