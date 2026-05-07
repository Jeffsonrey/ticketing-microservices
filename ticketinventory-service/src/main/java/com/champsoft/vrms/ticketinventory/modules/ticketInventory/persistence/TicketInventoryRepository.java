package com.champsoft.vrms.ticketinventory.modules.ticketInventory.persistence;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.port.out.TicketInventoryRepositoryPort;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketInventoryRepository extends JpaRepository<TicketInventory, Long>, TicketInventoryRepositoryPort {
    java.util.Optional<TicketInventory> findByEventIdAndTicketType(String eventId, String ticketType);
}
