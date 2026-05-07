package com.champsoft.vrms.event.modules.event.api.dto;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record UpdateEventRequest(
        String title,
        String description,
        String venueName,
        String venueAddress,
        String venueCity,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        @Min(value = 1, message = "totalCapacity must be greater than 0")
        Integer totalCapacity
) {}
