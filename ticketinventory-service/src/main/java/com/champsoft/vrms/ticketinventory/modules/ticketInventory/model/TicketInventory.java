package com.champsoft.vrms.ticketinventory.modules.ticketInventory.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ticket_inventory")
public class TicketInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_identifier", nullable = false, unique = true)
    private String inventoryIdentifier;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "ticket_type", nullable = false)
    private String ticketType;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;

    public TicketInventory() {
        this.inventoryIdentifier = UUID.randomUUID().toString();
    }

    public TicketInventory(String eventId, String ticketType, Integer totalTickets, Integer availableTickets) {
        this.inventoryIdentifier = UUID.randomUUID().toString();
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
    }

    public Long getId() {
        return id;
    }

    public String getInventoryIdentifier() {
        return inventoryIdentifier;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }
}