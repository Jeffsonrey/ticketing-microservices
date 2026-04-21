package com.champsoft.vrms.event.modules.event.application.port.out;

import com.champsoft.vrms.event.modules.event.domain.model.EventId;
import com.champsoft.vrms.event.modules.event.domain.model.EventListing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    EventListing save(EventListing event);
    Optional<EventListing> findById(EventId id);
    List<EventListing> findAll();
    void deleteById(EventId id);
    Optional<EventListing> findDuplicate(
            String title,
            String venueName,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

}
