package com.champsoft.vrms.event.modules.event.api.dto;

import java.time.LocalDateTime;

public record CreateEventRequest(
        String title,
        String description,
        String venueName,
        String venueAddress,
        String venueCity,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        int totalCapacity
) {}
