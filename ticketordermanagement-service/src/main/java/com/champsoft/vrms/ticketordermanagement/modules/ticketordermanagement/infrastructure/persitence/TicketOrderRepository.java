package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.persitence;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketOrderRepositoryPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long>, TicketOrderRepositoryPort {
}