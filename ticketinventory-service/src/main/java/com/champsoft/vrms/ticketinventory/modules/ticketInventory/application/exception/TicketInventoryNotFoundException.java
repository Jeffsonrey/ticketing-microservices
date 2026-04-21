package com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception;

public class TicketInventoryNotFoundException extends RuntimeException {

    public TicketInventoryNotFoundException(String message) {
        super(message);
    }
}