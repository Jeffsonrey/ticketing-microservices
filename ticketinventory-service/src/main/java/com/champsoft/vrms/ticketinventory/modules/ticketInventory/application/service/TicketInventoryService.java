package com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.service;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto.TicketInventoryRequestModel;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.DuplicateTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.InvalidTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.TicketInventoryNotFoundException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.port.out.TicketInventoryRepositoryPort;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketInventoryService {

    private final TicketInventoryRepositoryPort ticketInventoryRepositoryPort;

    public TicketInventoryService(TicketInventoryRepositoryPort ticketInventoryRepositoryPort) {
        this.ticketInventoryRepositoryPort = ticketInventoryRepositoryPort;
    }

    public List<TicketInventory> getAllInventories() {
        return ticketInventoryRepositoryPort.findAll();
    }

    public TicketInventory getInventoryById(Long id) {
        return ticketInventoryRepositoryPort.findById(id)
                .orElseThrow(() -> new TicketInventoryNotFoundException(
                        "Ticket inventory not found with id: " + id
                ));
    }

    public TicketInventory createInventory(TicketInventoryRequestModel requestModel) {
        validateInventory(requestModel);

        ticketInventoryRepositoryPort
                .findByEventIdAndTicketType(
                        requestModel.getEventId(),
                        requestModel.getTicketType()
                )
                .ifPresent(existing -> {
                    throw new DuplicateTicketInventoryException(
                            "A ticket inventory for this event and ticket type already exists."
                    );
                });

        TicketInventory ticketInventory = new TicketInventory(
                requestModel.getEventId(),
                requestModel.getTicketType(),
                requestModel.getTotalTickets(),
                requestModel.getAvailableTickets()
        );

        return ticketInventoryRepositoryPort.save(ticketInventory);
    }

    public TicketInventory updateInventory(Long id, TicketInventoryRequestModel requestModel) {
        validateInventory(requestModel);

        TicketInventory existingInventory = getInventoryById(id);
        existingInventory.setEventId(requestModel.getEventId());
        existingInventory.setTicketType(requestModel.getTicketType());
        existingInventory.setTotalTickets(requestModel.getTotalTickets());
        existingInventory.setAvailableTickets(requestModel.getAvailableTickets());

        return ticketInventoryRepositoryPort.save(existingInventory);
    }

    public void deleteInventory(Long id) {
        TicketInventory existingInventory = getInventoryById(id);
        ticketInventoryRepositoryPort.delete(existingInventory);
    }

    private void validateInventory(TicketInventoryRequestModel requestModel) {
        if (requestModel.getAvailableTickets() > requestModel.getTotalTickets()) {
            throw new InvalidTicketInventoryException(
                    "Available tickets cannot be greater than total tickets."
            );
        }
    }
}