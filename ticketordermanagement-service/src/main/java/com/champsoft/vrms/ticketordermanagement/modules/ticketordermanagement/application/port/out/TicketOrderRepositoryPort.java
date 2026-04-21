package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;

import java.util.List;
import java.util.Optional;

public interface TicketOrderRepositoryPort {

    List<TicketOrder> findAll();

    Optional<TicketOrder> findById(Long id);

    TicketOrder save(TicketOrder ticketOrder);

    void delete(TicketOrder ticketOrder);
}