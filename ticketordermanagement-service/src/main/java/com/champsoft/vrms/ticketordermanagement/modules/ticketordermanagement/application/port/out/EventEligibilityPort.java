package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out;

public interface EventEligibilityPort {
    boolean isEligible(String eventId);
}
