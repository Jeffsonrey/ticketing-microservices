package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import com.champsoft.vrms.event.modules.event.application.port.out.EventRepositoryPort;
import com.champsoft.vrms.event.modules.event.domain.model.EventId;
import com.champsoft.vrms.event.modules.event.domain.model.EventListing;
import com.champsoft.vrms.event.modules.event.domain.model.EventSchedule;
import com.champsoft.vrms.event.modules.event.domain.model.EventStatus;
import com.champsoft.vrms.event.modules.event.domain.model.Venue;
import com.champsoft.vrms.event.modules.event.domain.model.VenueId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaEventRepositoryAdapter implements EventRepositoryPort {

    private final SpringDataEventRepository jpa;

    public JpaEventRepositoryAdapter(SpringDataEventRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public EventListing save(EventListing event) {
        var entity = toEntity(event);
        jpa.save(entity);
        return event;
    }

    @Override
    public Optional<EventListing> findDuplicate(
            String title,
            String venueName,
            java.time.LocalDateTime startDateTime,
            java.time.LocalDateTime endDateTime
    ) {
        return jpa.findByTitleAndVenue_NameAndSchedule_StartDateTimeAndSchedule_EndDateTime(
                title,
                venueName,
                startDateTime,
                endDateTime
        ).map(this::toDomain);
    }

    @Override
    public Optional<EventListing> findById(EventId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<EventListing> findAll() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(EventId id) {
        jpa.deleteById(id.value());
    }

    private EventJpaEntity toEntity(EventListing event) {
        var entity = new EventJpaEntity();
        entity.id = event.getId().value();
        entity.title = event.getTitle();
        entity.description = event.getDescription();

        entity.venue = new VenueEmbeddable(
                event.getVenue().getVenueId().value(),
                event.getVenue().getName(),
                event.getVenue().getAddress(),
                event.getVenue().getCity()
        );

        entity.schedule = new EventScheduleEmbeddable(
                event.getSchedule().startDateTime(),
                event.getSchedule().endDateTime()
        );

        entity.totalCapacity = event.getTotalCapacity();
        entity.availableTickets = event.getAvailableTickets();
        entity.status = event.getStatus().name();

        return entity;
    }

    private EventListing toDomain(EventJpaEntity entity) {
        var event = new EventListing(
                EventId.of(entity.id),
                entity.title,
                entity.description,
                new Venue(
                        VenueId.of(entity.venue.venueId),
                        entity.venue.name,
                        entity.venue.address,
                        entity.venue.city
                ),
                new EventSchedule(
                        entity.schedule.startDateTime,
                        entity.schedule.endDateTime
                ),
                entity.totalCapacity,
                entity.availableTickets
        );

        if (EventStatus.ACTIVE.name().equalsIgnoreCase(entity.status)) {
            event.activate();
        } else if (EventStatus.CANCELLED.name().equalsIgnoreCase(entity.status)) {
            event.cancel();
        }

        return event;
    }
}