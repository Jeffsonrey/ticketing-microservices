package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out;

public interface CustomerEligibilityPort {
    boolean isEligible(String customerId);
}
