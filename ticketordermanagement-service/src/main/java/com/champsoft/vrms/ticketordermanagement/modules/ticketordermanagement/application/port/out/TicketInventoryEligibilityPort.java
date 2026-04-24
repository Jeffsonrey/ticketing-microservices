package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out;

public interface TicketInventoryEligibilityPort {
    boolean isEligible(String eventId, String ticketType, Integer quantity);
}
