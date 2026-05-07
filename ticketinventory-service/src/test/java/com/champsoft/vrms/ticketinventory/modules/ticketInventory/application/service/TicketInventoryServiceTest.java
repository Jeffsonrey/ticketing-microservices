package com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.service;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto.TicketInventoryRequestModel;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.DuplicateTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.InvalidTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.TicketInventoryNotFoundException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.port.out.TicketInventoryRepositoryPort;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketInventoryServiceTest {

    @Mock
    private TicketInventoryRepositoryPort repository;

    @InjectMocks
    private TicketInventoryService service;

    @Test
    void createInventorySavesWhenValid() {
        when(repository.save(any(TicketInventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketInventory saved = service.createInventory(request("event-1", "VIP", 100, 50));

        assertThat(saved.getEventId()).isEqualTo("event-1");
    }

    @Test
    void createInventoryRejectsDuplicateAndInvalidValues() {
        when(repository.findByEventIdAndTicketType("event-1", "VIP"))
                .thenReturn(Optional.of(new TicketInventory("event-1", "VIP", 100, 50)));

        assertThatThrownBy(() -> service.createInventory(request("event-1", "VIP", 100, 50)))
                .isInstanceOf(DuplicateTicketInventoryException.class);

        assertThatThrownBy(() -> service.createInventory(request("event-2", "VIP", 0, 0)))
                .isInstanceOf(InvalidTicketInventoryException.class);
    }

    @Test
    void serviceSupportsLookupDeleteAndEligibilityChecks() {
        TicketInventory inventory = new TicketInventory("event-1", "VIP", 100, 5);
        when(repository.findById(1L)).thenReturn(Optional.of(inventory));
        when(repository.findById(2L)).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of(inventory));
        when(repository.findByEventIdAndTicketType("event-1", "VIP")).thenReturn(Optional.of(inventory));
        when(repository.findByEventIdAndTicketType("event-2", "VIP")).thenReturn(Optional.empty());

        assertThat(service.getAllInventories()).containsExactly(inventory);
        assertThat(service.isEligible(1L)).isTrue();
        assertThat(service.canFulfill("event-1", "VIP", 3)).isTrue();
        assertThat(service.canFulfill("event-2", "VIP", 3)).isFalse();
        assertThatThrownBy(() -> service.getInventoryById(2L)).isInstanceOf(TicketInventoryNotFoundException.class);

        service.deleteInventory(1L);
        verify(repository).delete(inventory);
    }

    private TicketInventoryRequestModel request(String eventId, String ticketType, int totalTickets, int availableTickets) {
        TicketInventoryRequestModel request = new TicketInventoryRequestModel();
        request.setEventId(eventId);
        request.setTicketType(ticketType);
        request.setTotalTickets(totalTickets);
        request.setAvailableTickets(availableTickets);
        return request;
    }
}
