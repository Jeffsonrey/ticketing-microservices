package com.champsoft.vrms.event.modules.event.application.service;


import com.champsoft.vrms.event.modules.event.application.exception.EventNotFoundException;
import com.champsoft.vrms.event.modules.event.application.exception.DuplicateEventException;
import com.champsoft.vrms.event.modules.event.application.port.out.EventRepositoryPort;
import com.champsoft.vrms.event.modules.event.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventCrudService {

    private final EventRepositoryPort repo;

    public EventCrudService(EventRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional
    public EventListing create(
            String title,
            String description,
            String venueName,
            String venueAddress,
            String venueCity,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            int totalCapacity
    ) {
        var venue = new Venue(
                VenueId.newId(),
                venueName,
                venueAddress,
                venueCity
        );

        var schedule = new EventSchedule(startDateTime, endDateTime);

        repo.findDuplicate(title, venueName, startDateTime, endDateTime)
                .ifPresent(existing -> {
                    throw new DuplicateEventException("An event with the same title, venue, and schedule already exists.");
                });

        var event = new EventListing(
                EventId.newId(),
                title,
                description,
                venue,
                schedule,
                totalCapacity,
                totalCapacity
        );

        return repo.save(event);
    }

    @Transactional(readOnly = true)
    public EventListing getById(String id) {
        return repo.findById(EventId.of(id))
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<EventListing> list() {
        return repo.findAll();
    }

    @Transactional
    public EventListing update(
            String id,
            String title,
            String description,
            String venueName,
            String venueAddress,
            String venueCity,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer totalCapacity
    ) {
        var event = getById(id);

        var effectiveTitle = title != null ? title : event.getTitle();
        var effectiveVenueName = venueName != null ? venueName : event.getVenue().getName();
        var effectiveStart = startDateTime != null ? startDateTime : event.getSchedule().startDateTime();
        var effectiveEnd = endDateTime != null ? endDateTime : event.getSchedule().endDateTime();

        repo.findDuplicate(effectiveTitle, effectiveVenueName, effectiveStart, effectiveEnd)
                .filter(existing -> !existing.getId().equals(event.getId()))
                .ifPresent(existing -> {
                    throw new DuplicateEventException("An event with the same title, venue, and schedule already exists.");
                });

        if (title != null && !title.isBlank()) {
            event.updateTitle(title);
        }

        if (description != null && !description.isBlank()) {
            event.updateDescription(description);
        }

        if (venueName != null || venueAddress != null || venueCity != null) {
            event.updateVenue(
                    venueName != null ? venueName : event.getVenue().getName(),
                    venueAddress != null ? venueAddress : event.getVenue().getAddress(),
                    venueCity != null ? venueCity : event.getVenue().getCity()
            );
        }

        if (startDateTime != null || endDateTime != null) {
            var currentSchedule = event.getSchedule();

            var newStart = startDateTime != null ? startDateTime : currentSchedule.startDateTime();
            var newEnd = endDateTime != null ? endDateTime : currentSchedule.endDateTime();

            event.reschedule(new EventSchedule(newStart, newEnd));
        }

        if (totalCapacity != null) {
            event.changeCapacity(totalCapacity);
        }

        return repo.save(event);
    }

    @Transactional
    public EventListing activate(String id) {
        var event = getById(id);
        event.activate();
        return repo.save(event);
    }

    @Transactional
    public EventListing cancel(String id) {
        var event = getById(id);
        event.cancel();
        return repo.save(event);
    }

    @Transactional
    public void delete(String id) {
        getById(id);
        repo.deleteById(EventId.of(id));
    }
}
