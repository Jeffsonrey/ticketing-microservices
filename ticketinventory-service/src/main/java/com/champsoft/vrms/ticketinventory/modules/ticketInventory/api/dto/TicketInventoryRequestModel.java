package com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketInventoryRequestModel {

    @NotBlank(message = "eventId is required")
    private String eventId;

    @NotBlank(message = "ticketType is required")
    private String ticketType;

    @NotNull(message = "totalTickets is required")
    @Min(value = 0, message = "totalTickets must be 0 or more")
    private Integer totalTickets;

    @NotNull(message = "availableTickets is required")
    @Min(value = 0, message = "availableTickets must be 0 or more")
    private Integer availableTickets;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }
}