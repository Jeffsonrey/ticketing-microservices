package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.service;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderRequestModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.InvalidTicketOrderException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.TicketOrderNotFoundException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.CustomerEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.EventEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketInventoryEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketOrderRepositoryPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketOrderServiceTest {

    @Mock
    private TicketOrderRepositoryPort repository;

    @Mock
    private CustomerEligibilityPort customerEligibilityPort;

    @Mock
    private EventEligibilityPort eventEligibilityPort;

    @Mock
    private TicketInventoryEligibilityPort ticketInventoryEligibilityPort;

    @InjectMocks
    private TicketOrderService service;

    @Test
    void createOrderSucceedsWhenAllDependenciesAreEligible() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2)).thenReturn(true);
        when(repository.save(any(TicketOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketOrder saved = service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING"));

        assertThat(saved.getEventId()).isEqualTo("event-1");
    }

    @Test
    void createOrderRejectsCustomerThatIsNotEligible() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(false);

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(InvalidTicketOrderException.class)
                .hasMessage("Customer is not eligible to place an order.");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderRejectsEventThatIsNotEligible() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(false);

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(InvalidTicketOrderException.class)
                .hasMessage("Event is not eligible for ticket ordering.");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderRejectsInventoryThatIsNotEligible() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2)).thenReturn(false);

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(InvalidTicketOrderException.class)
                .hasMessage("Requested ticket inventory is not available.");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderPropagatesCustomerDownstreamFailure() {
        when(customerEligibilityPort.isEligible("1")).thenThrow(new RestClientException("customer-service down"));

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(RestClientException.class)
                .hasMessage("customer-service down");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderPropagatesEventDownstreamFailure() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenThrow(new RestClientException("404 Not Found from event-service"));

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(RestClientException.class)
                .hasMessage("404 Not Found from event-service");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderPropagatesInventoryDownstreamFailure() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2))
                .thenThrow(new RestClientException("inventory-service unavailable"));

        assertThatThrownBy(() -> service.createOrder(request(1L, "event-1", "VIP", 2, "PENDING")))
                .isInstanceOf(RestClientException.class)
                .hasMessage("inventory-service unavailable");
        verify(repository, never()).save(any());
    }

    @Test
    void createOrderRejectsInvalidQuantityAndTotalPriceBeforeCallingDownstreamServices() {
        TicketOrderRequestModel invalidQuantity = request(1L, "event-1", "VIP", 0, "PENDING");
        TicketOrderRequestModel invalidPrice = request(1L, "event-1", "VIP", 2, "PENDING");
        invalidPrice.setTotalPrice(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.createOrder(invalidQuantity))
                .isInstanceOf(InvalidTicketOrderException.class)
                .hasMessage("Quantity must be greater than 0.");
        assertThatThrownBy(() -> service.createOrder(invalidPrice))
                .isInstanceOf(InvalidTicketOrderException.class)
                .hasMessage("Total price must be greater than 0.");

        verify(customerEligibilityPort, never()).isEligible(any());
        verify(eventEligibilityPort, never()).isEligible(any());
        verify(ticketInventoryEligibilityPort, never()).isEligible(any(), any(), any(Integer.class));
        verify(repository, never()).save(any());
    }

    @Test
    void updateOrderDoesNotSaveWhenRepositoryLookupFails() {
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 3)).thenReturn(true);
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(42L, request(1L, "event-1", "VIP", 3, "CONFIRMED")))
                .isInstanceOf(TicketOrderNotFoundException.class)
                .hasMessage("Ticket order not found with id: 42");
        verify(repository, never()).save(any());
    }

    @Test
    void lookupListUpdateAndDeleteUseRepository() {
        TicketOrder existing = new TicketOrder(1L, "event-1", "VIP", 2, BigDecimal.valueOf(50), "PENDING");
        when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 3)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findById(2L)).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of(existing));
        when(repository.save(any(TicketOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.getAllOrders()).containsExactly(existing);
        assertThat(service.getOrderById(1L)).isSameAs(existing);

        TicketOrder updated = service.updateOrder(1L, request(1L, "event-1", "VIP", 3, "CONFIRMED"));
        assertThat(updated.getQuantity()).isEqualTo(3);

        service.deleteOrder(1L);
        verify(repository).delete(existing);

        assertThatThrownBy(() -> service.getOrderById(2L)).isInstanceOf(TicketOrderNotFoundException.class);
    }

    @Test
    void deleteOrderThrowsWhenRepositoryLookupFails() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteOrder(99L))
                .isInstanceOf(TicketOrderNotFoundException.class)
                .hasMessage("Ticket order not found with id: 99");
    }

    private TicketOrderRequestModel request(Long customerId, String eventId, String ticketType, int quantity, String status) {
        TicketOrderRequestModel request = new TicketOrderRequestModel();
        request.setCustomerId(customerId);
        request.setEventId(eventId);
        request.setTicketType(ticketType);
        request.setQuantity(quantity);
        request.setTotalPrice(BigDecimal.valueOf(99.99));
        request.setStatus(status);
        return request;
    }
}
