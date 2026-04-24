package com.champsoft.vrms.ticketinventory.modules.ticketInventory.api;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto.TicketInventoryRequestModel;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.dto.TicketInventoryResponseModel;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.api.mapper.TicketInventoryResponseMapper;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.service.TicketInventoryService;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.model.TicketInventory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-inventories")
public class TicketInventoryController {

    private final TicketInventoryService ticketInventoryService;

    public TicketInventoryController(TicketInventoryService ticketInventoryService) {
        this.ticketInventoryService = ticketInventoryService;
    }

    @GetMapping
    public ResponseEntity<List<TicketInventoryResponseModel>> getAllInventories() {
        List<TicketInventoryResponseModel> response = ticketInventoryService.getAllInventories()
                .stream()
                .map(TicketInventoryResponseMapper::toResponseModel)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketInventoryResponseModel> getInventoryById(@PathVariable Long id) {
        TicketInventory ticketInventory = ticketInventoryService.getInventoryById(id);
        return ResponseEntity.ok(TicketInventoryResponseMapper.toResponseModel(ticketInventory));
    }

    @PostMapping
    public ResponseEntity<TicketInventoryResponseModel> createInventory(
            @Valid @RequestBody TicketInventoryRequestModel requestModel) {

        TicketInventory createdInventory = ticketInventoryService.createInventory(requestModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketInventoryResponseMapper.toResponseModel(createdInventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketInventoryResponseModel> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody TicketInventoryRequestModel requestModel) {

        TicketInventory updatedInventory = ticketInventoryService.updateInventory(id, requestModel);
        return ResponseEntity.ok(TicketInventoryResponseMapper.toResponseModel(updatedInventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        ticketInventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eligibility")
    public ResponseEntity<Boolean> isEligible(
            @RequestParam String eventId,
            @RequestParam String ticketType,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(ticketInventoryService.isEligible(eventId, ticketType, quantity));
    }
}