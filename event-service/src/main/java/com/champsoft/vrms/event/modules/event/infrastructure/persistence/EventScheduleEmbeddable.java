package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class EventScheduleEmbeddable {

    @Column(name = "start_date_time", nullable = false)
    public LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    public LocalDateTime endDateTime;

    protected EventScheduleEmbeddable() {
    }

    public EventScheduleEmbeddable(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}