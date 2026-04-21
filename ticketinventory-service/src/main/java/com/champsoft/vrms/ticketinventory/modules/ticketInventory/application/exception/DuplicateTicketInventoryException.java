package com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception;

public class DuplicateTicketInventoryException extends RuntimeException {
    public DuplicateTicketInventoryException(String message) {
        super(message);
    }
}