package com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto;

public class TicketInventoryResponseModel {

    private Long id;
    private String inventoryIdentifier;
    private String eventId;
    private String ticketType;
    private Integer totalTickets;
    private Integer availableTickets;

    public TicketInventoryResponseModel(Long id, String inventoryIdentifier, String eventId,
                                        String ticketType, Integer totalTickets, Integer availableTickets) {
        this.id = id;
        this.inventoryIdentifier = inventoryIdentifier;
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
}