package com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.mapper;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto.TicketInventoryResponseModel;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;

public class TicketInventoryResponseMapper {

    private TicketInventoryResponseMapper() {
    }

    public static TicketInventoryResponseModel toResponseModel(TicketInventory ticketInventory) {
        return new TicketInventoryResponseModel(
                ticketInventory.getId(),
                ticketInventory.getInventoryIdentifier(),
                ticketInventory.getEventId(),
                ticketInventory.getTicketType(),
                ticketInventory.getTotalTickets(),
                ticketInventory.getAvailableTickets()
        );
    }
}
