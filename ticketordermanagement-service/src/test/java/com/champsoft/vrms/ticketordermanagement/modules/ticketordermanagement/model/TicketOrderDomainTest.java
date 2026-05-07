package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TicketOrderDomainTest {

    @Test
    void ticketOrderGeneratesIdentifierAndAllowsStateChanges() {
        TicketOrder order = new TicketOrder(1L, "event-1", "VIP", 2, BigDecimal.valueOf(99.99), "PENDING");

        assertThat(order.getOrderIdentifier()).isNotBlank();

        order.setStatus("CONFIRMED");
        order.setQuantity(3);

        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
        assertThat(order.getQuantity()).isEqualTo(3);
    }
}
