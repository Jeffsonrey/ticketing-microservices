package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.service;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderRequestModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.InvalidTicketOrderException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.TicketOrderNotFoundException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.CustomerEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.EventEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketInventoryEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketOrderRepositoryPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketOrderService {

    private final TicketOrderRepositoryPort ticketOrderRepositoryPort;
    private final CustomerEligibilityPort customerEligibilityPort;
    private final EventEligibilityPort eventEligibilityPort;
    private final TicketInventoryEligibilityPort ticketInventoryEligibilityPort;

    public TicketOrderService(
            TicketOrderRepositoryPort ticketOrderRepositoryPort,
            CustomerEligibilityPort customerEligibilityPort,
            EventEligibilityPort eventEligibilityPort,
            TicketInventoryEligibilityPort ticketInventoryEligibilityPort
    ) {
        this.ticketOrderRepositoryPort = ticketOrderRepositoryPort;
        this.customerEligibilityPort = customerEligibilityPort;
        this.eventEligibilityPort = eventEligibilityPort;
        this.ticketInventoryEligibilityPort = ticketInventoryEligibilityPort;
    }

    public List<TicketOrder> getAllOrders() {
        return ticketOrderRepositoryPort.findAll();
    }

    public TicketOrder getOrderById(Long id) {
        return ticketOrderRepositoryPort.findById(id)
                .orElseThrow(() -> new TicketOrderNotFoundException(
                        "Ticket order not found with id: " + id
                ));
    }

    public TicketOrder createOrder(TicketOrderRequestModel requestModel) {

        validateOrder(requestModel);

        TicketOrder ticketOrder = new TicketOrder(
                requestModel.getCustomerId(),
                requestModel.getEventId(),
                requestModel.getTicketType(),
                requestModel.getQuantity(),
                requestModel.getTotalPrice(),
                requestModel.getStatus()
        );

        return ticketOrderRepositoryPort.save(ticketOrder);
    }

    public TicketOrder updateOrder(Long id, TicketOrderRequestModel requestModel) {
        validateOrder(requestModel);

        TicketOrder existingOrder = getOrderById(id);
        existingOrder.setCustomerId(requestModel.getCustomerId());
        existingOrder.setEventId(requestModel.getEventId());
        existingOrder.setTicketType(requestModel.getTicketType());
        existingOrder.setQuantity(requestModel.getQuantity());
        existingOrder.setTotalPrice(requestModel.getTotalPrice());
        existingOrder.setStatus(requestModel.getStatus());

        return ticketOrderRepositoryPort.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        TicketOrder existingOrder = getOrderById(id);
        ticketOrderRepositoryPort.delete(existingOrder);
    }

    private void validateOrder(TicketOrderRequestModel requestModel) {
        if (requestModel.getQuantity() <= 0) {
            throw new InvalidTicketOrderException("Quantity must be greater than 0.");
        }

        if (requestModel.getTotalPrice().doubleValue() <= 0) {
            throw new InvalidTicketOrderException("Total price must be greater than 0.");
        }

        if (!customerEligibilityPort.isEligible(String.valueOf(requestModel.getCustomerId()))) {
            throw new InvalidTicketOrderException("Customer is not eligible to place an order.");
        }

        if (!eventEligibilityPort.isEligible(requestModel.getEventId())) {
            throw new InvalidTicketOrderException("Event is not eligible for ticket ordering.");
        }

        if (!ticketInventoryEligibilityPort.isEligible(
                requestModel.getEventId(),
                requestModel.getTicketType(),
                requestModel.getQuantity()
        )) {
            throw new InvalidTicketOrderException("Requested ticket inventory is not available.");
        }
    }
}
