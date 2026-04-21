package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ticket_orders")
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_identifier", nullable = false, unique = true)
    private String orderIdentifier;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "ticket_type", nullable = false)
    private String ticketType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "status", nullable = false)
    private String status;

    public TicketOrder() {
        this.orderIdentifier = UUID.randomUUID().toString();
    }

    public TicketOrder(Long customerId, String eventId, String ticketType,
                       Integer quantity, BigDecimal totalPrice, String status) {
        this.orderIdentifier = UUID.randomUUID().toString();
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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
