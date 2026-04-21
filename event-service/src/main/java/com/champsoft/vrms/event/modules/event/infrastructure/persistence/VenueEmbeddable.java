package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VenueEmbeddable {

    @Column(name = "venue_id", nullable = false)
    public String venueId;

    @Column(name = "venue_name", nullable = false)
    public String name;

    @Column(name = "venue_address", nullable = false)
    public String address;

    @Column(name = "venue_city", nullable = false)
    public String city;

    protected VenueEmbeddable() {
    }

    public VenueEmbeddable(String venueId, String name, String address, String city) {
        this.venueId = venueId;
        this.name = name;
        this.address = address;
        this.city = city;
    }
}