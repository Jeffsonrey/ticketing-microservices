package com.champsoft.vrms.event.modules.event.domain.model;

import java.util.Objects;
import java.util.UUID;

public class EventId {
    private final String value;

    private EventId(final String value) { this.value = value; }

    public static EventId newId() {return new EventId(UUID.randomUUID().toString()); }
    public static EventId of(String value) { return new EventId(value); }
    public String value() { return value; }

    @Override public boolean equals(final Object o) {
        return o instanceof EventId && value.equals(((EventId) o).value());
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
