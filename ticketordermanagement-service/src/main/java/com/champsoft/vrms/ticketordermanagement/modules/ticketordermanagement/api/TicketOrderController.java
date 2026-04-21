package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderRequestModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto.TicketOrderResponseModel;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.mapper.TicketOrderResponseMapper;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.service.TicketOrderService;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model.TicketOrder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-orders")
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    public TicketOrderController(TicketOrderService ticketOrderService) {
        this.ticketOrderService = ticketOrderService;
    }

    @GetMapping
    public ResponseEntity<List<TicketOrderResponseModel>> getAllOrders() {
        List<TicketOrderResponseModel> response = ticketOrderService.getAllOrders()
                .stream()
                .map(TicketOrderResponseMapper::toResponseModel)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketOrderResponseModel> getOrderById(@PathVariable Long id) {
        TicketOrder ticketOrder = ticketOrderService.getOrderById(id);
        return ResponseEntity.ok(TicketOrderResponseMapper.toResponseModel(ticketOrder));
    }

    @PostMapping
    public ResponseEntity<TicketOrderResponseModel> createOrder(
            @Valid @RequestBody TicketOrderRequestModel requestModel) {

        TicketOrder createdOrder = ticketOrderService.createOrder(requestModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketOrderResponseMapper.toResponseModel(createdOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketOrderResponseModel> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody TicketOrderRequestModel requestModel) {

        TicketOrder updatedOrder = ticketOrderService.updateOrder(id, requestModel);
        return ResponseEntity.ok(TicketOrderResponseMapper.toResponseModel(updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        ticketOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}