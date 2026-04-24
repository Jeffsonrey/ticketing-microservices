package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api.dto;

import java.math.BigDecimal;
import org.springframework.hateoas.RepresentationModel;

public class TicketOrderResponseModel extends RepresentationModel<TicketOrderResponseModel> {

    private Long id;
    private String orderIdentifier;
    private Long customerId;
    private String eventId;
    private String ticketType;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;

    public TicketOrderResponseModel(Long id, String orderIdentifier, Long customerId,
                                    String eventId, String ticketType, Integer quantity,
                                    BigDecimal totalPrice, String status) {
        this.id = id;
        this.orderIdentifier = orderIdentifier;
        this.customerId = customerId;
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}