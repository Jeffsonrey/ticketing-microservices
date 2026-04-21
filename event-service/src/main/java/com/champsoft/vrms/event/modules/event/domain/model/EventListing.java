package com.champsoft.vrms.event.modules.event.domain.model;

import java.util.Objects;

public class EventListing {
    private final EventId id;
    private String title;
    private String description;
    private Venue venue;
    private EventSchedule schedule;
    private int totalCapacity;
    private int availableTickets;
    private EventStatus status;

    public EventListing(
            EventId id,
            String title,
            String description,
            Venue venue,
            EventSchedule schedule,
            int totalCapacity,
            int availableTickets
    ) {
        this.id = Objects.requireNonNull(id, "Event id cannot be null");
        this.title = requireText(title, "Title cannot be blank");
        this.description = requireText(description, "Description cannot be blank");
        this.venue = Objects.requireNonNull(venue, "Venue cannot be null");
        this.schedule = Objects.requireNonNull(schedule, "Schedule cannot be null");

        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("Total capacity must be greater than 0");
        }

        if (availableTickets < 0) {
            throw new IllegalArgumentException("Available tickets cannot be negative");
        }

        if (availableTickets > totalCapacity) {
            throw new IllegalArgumentException("Available tickets cannot exceed total capacity");
        }

        this.totalCapacity = totalCapacity;
        this.availableTickets = availableTickets;
        this.status = EventStatus.INACTIVE;

        validateInvariants();
    }

    public EventId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Venue getVenue() {
        return venue;
    }

    public EventSchedule getSchedule() {
        return schedule;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void activate() {
        if (this.status == EventStatus.ACTIVE) {
            throw new IllegalStateException("Event is already active");
        }

        if (this.status == EventStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled event cannot be activated");
        }

        this.status = EventStatus.ACTIVE;
    }

    public void cancel() {
        if (this.status == EventStatus.CANCELLED) {
            throw new IllegalStateException("Event is already cancelled");
        }

        this.status = EventStatus.CANCELLED;
    }

    public void updateVenue(String name, String address, String city) {
        ensureNotCancelled();
        this.venue.updateDetails(name, address, city);
        validateInvariants();
    }

    public void reschedule(EventSchedule newSchedule) {
        ensureNotCancelled();
        this.schedule = Objects.requireNonNull(newSchedule, "Schedule cannot be null");
        validateInvariants();
    }

    public void reserveTickets(int quantity) {
        ensureNotCancelled();

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (availableTickets < quantity) {
            throw new IllegalStateException("Not enough available tickets");
        }

        this.availableTickets -= quantity;
        validateInvariants();
    }

    public void releaseTickets(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        this.availableTickets += quantity;
        validateInvariants();
    }

    public void changeCapacity(int newTotalCapacity) {
        ensureNotCancelled();

        if (newTotalCapacity <= 0) {
            throw new IllegalArgumentException("Total capacity must be greater than 0");
        }

        int reservedTickets = this.totalCapacity - this.availableTickets;

        if (newTotalCapacity < reservedTickets) {
            throw new IllegalStateException("New capacity cannot be less than reserved tickets");
        }

        this.totalCapacity = newTotalCapacity;
        this.availableTickets = newTotalCapacity - reservedTickets;

        validateInvariants();
    }

    private void ensureNotCancelled() {
        if (this.status == EventStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled event cannot be modified");
        }
    }

    private void validateInvariants() {
        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Title cannot be blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalStateException("Description cannot be blank");
        }

        if (venue == null) {
            throw new IllegalStateException("Venue cannot be null");
        }

        if (schedule == null) {
            throw new IllegalStateException("Schedule cannot be null");
        }

        if (totalCapacity <= 0) {
            throw new IllegalStateException("Total capacity must be greater than 0");
        }

        if (availableTickets < 0) {
            throw new IllegalStateException("Available tickets cannot be negative");
        }

        if (availableTickets > totalCapacity) {
            throw new IllegalStateException("Available tickets cannot exceed total capacity");
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
    public void updateTitle(String newTitle) {
        ensureNotCancelled();

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        this.title = newTitle;
    }

    public void updateDescription(String newDescription) {
        ensureNotCancelled();

        if (newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }

        this.description = newDescription;
    }
    public boolean isEligibleForTicketing() {
        return status == EventStatus.ACTIVE
                && availableTickets > 0;
    }
}