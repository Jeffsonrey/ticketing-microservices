package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class EventJpaEntity {

    @Id
    public String id;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false, length = 2000)
    public String description;

    @Embedded
    public VenueEmbeddable venue;

    @Embedded
    public EventScheduleEmbeddable schedule;

    @Column(nullable = false)
    public int totalCapacity;

    @Column(nullable = false)
    public int availableTickets;

    @Column(nullable = false)
    public String status;
}