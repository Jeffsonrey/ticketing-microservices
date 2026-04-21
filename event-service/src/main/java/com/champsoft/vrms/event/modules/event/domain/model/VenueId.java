package com.champsoft.vrms.event.modules.event.domain.model;

import java.util.Objects;
import java.util.UUID;

public class VenueId {
    private final String value;

    private VenueId(final String value) { this.value = value; }

    public static VenueId newId() {return new VenueId(UUID.randomUUID().toString()); }
    public static VenueId of(String value) { return new VenueId(value); }
    public String value() { return value; }

    @Override public boolean equals(final Object o) {
        return o instanceof VenueId && value.equals(((VenueId) o).value());
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
