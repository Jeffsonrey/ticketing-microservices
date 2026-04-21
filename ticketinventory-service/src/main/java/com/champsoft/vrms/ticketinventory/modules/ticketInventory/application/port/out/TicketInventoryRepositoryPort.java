package com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.port.out;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;

import java.util.List;
import java.util.Optional;

public interface TicketInventoryRepositoryPort {

    List<TicketInventory> findAll();

    Optional<TicketInventory> findById(Long id);

    TicketInventory save(TicketInventory ticketInventory);

    Optional<TicketInventory> findByEventIdAndTicketType(String eventId, String ticketType);

    void delete(TicketInventory ticketInventory);
}