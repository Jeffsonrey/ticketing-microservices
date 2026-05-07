package com.champsoft.vrms.event.modules.event.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "description is required")
        String description,
        @NotBlank(message = "venueName is required")
        String venueName,
        @NotBlank(message = "venueAddress is required")
        String venueAddress,
        @NotBlank(message = "venueCity is required")
        String venueCity,
        @NotNull(message = "startDateTime is required")
        LocalDateTime startDateTime,
        @NotNull(message = "endDateTime is required")
        LocalDateTime endDateTime,
        @Min(value = 1, message = "totalCapacity must be greater than 0")
        int totalCapacity
) {}
