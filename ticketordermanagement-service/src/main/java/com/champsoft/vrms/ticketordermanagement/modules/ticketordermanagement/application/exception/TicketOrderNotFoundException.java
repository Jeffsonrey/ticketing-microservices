package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception;

public class TicketOrderNotFoundException extends RuntimeException {

    public TicketOrderNotFoundException(String message) {
        super(message);
    }
}