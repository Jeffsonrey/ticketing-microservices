package com.champsoft.vrms.ticketinventory.modules.ticketInventory.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketInventoryDomainTest {

    @Test
    void inventoryEligibilityAndCapacityChecksWork() {
        TicketInventory inventory = new TicketInventory("event-1", "VIP", 10, 5);

        assertThat(inventory.isEligible()).isTrue();
        assertThat(inventory.canFulfill(4)).isTrue();
        assertThat(inventory.canFulfill(6)).isFalse();
    }

    @Test
    void quantityMustBePositive() {
        TicketInventory inventory = new TicketInventory("event-1", "VIP", 10, 5);

        assertThatThrownBy(() -> inventory.canFulfill(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
