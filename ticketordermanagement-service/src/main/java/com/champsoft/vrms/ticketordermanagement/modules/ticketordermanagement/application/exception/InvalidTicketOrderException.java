package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception;

public class InvalidTicketOrderException extends RuntimeException {

    public InvalidTicketOrderException(String message) {
        super(message);
    }
}