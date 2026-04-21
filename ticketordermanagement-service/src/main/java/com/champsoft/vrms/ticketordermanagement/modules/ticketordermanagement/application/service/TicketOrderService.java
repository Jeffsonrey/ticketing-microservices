package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.service;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderRequestModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.InvalidTicketOrderException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.TicketOrderNotFoundException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketOrderRepositoryPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketOrderService {

    private final TicketOrderRepositoryPort ticketOrderRepositoryPort;

    public TicketOrderService(TicketOrderRepositoryPort ticketOrderRepositoryPort) {
        this.ticketOrderRepositoryPort = ticketOrderRepositoryPort;
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
    }
}