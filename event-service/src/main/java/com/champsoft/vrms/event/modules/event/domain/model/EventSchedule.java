package com.champsoft.vrms.event.modules.event.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record EventSchedule(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public EventSchedule {
        Objects.requireNonNull(startDateTime, "startDateTime cannot be null");
        Objects.requireNonNull(endDateTime, "endDateTime cannot be null");

        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End date/time must be after start date/time");
        }
    }
}