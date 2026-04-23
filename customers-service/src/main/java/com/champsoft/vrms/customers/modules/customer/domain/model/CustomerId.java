package com.champsoft.vrms.customers.modules.customer.domain.model;

public record CustomerId(Long value) {

    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }
}