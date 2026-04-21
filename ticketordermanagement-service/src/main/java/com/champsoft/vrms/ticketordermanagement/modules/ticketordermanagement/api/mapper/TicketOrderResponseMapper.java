package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.mapper;


import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderResponseModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;

public class TicketOrderResponseMapper {

    private TicketOrderResponseMapper() {
    }

    public static TicketOrderResponseModel toResponseModel(TicketOrder ticketOrder) {
        return new TicketOrderResponseModel(
                ticketOrder.getId(),
                ticketOrder.getOrderIdentifier(),
                ticketOrder.getCustomerId(),
                ticketOrder.getEventId(),
                ticketOrder.getTicketType(),
                ticketOrder.getQuantity(),
                ticketOrder.getTotalPrice(),
                ticketOrder.getStatus()
        );
    }
}
