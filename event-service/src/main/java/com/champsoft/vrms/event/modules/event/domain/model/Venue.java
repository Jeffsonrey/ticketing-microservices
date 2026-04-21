package com.champsoft.vrms.event.modules.event.domain.model;

import java.util.Objects;

public class Venue {

    private final VenueId venueId;
    private String name;
    private String address;
    private String city;

    public Venue(VenueId venueId, String name, String address, String city) {
        this.venueId = Objects.requireNonNull(venueId, "venueId cannot be null");
        this.name = requireText(name, "name");
        this.address = requireText(address, "address");
        this.city = requireText(city, "city");
    }

    public VenueId getVenueId() {
        return venueId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    void updateDetails(String name, String address, String city) {
        this.name = requireText(name, "name");
        this.address = requireText(address, "address");
        this.city = requireText(city, "city");
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value;
    }
}