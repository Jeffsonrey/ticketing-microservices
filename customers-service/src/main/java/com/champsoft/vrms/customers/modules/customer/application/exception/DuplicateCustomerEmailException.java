package com.champsoft.vrms.customers.modules.customer.application.exception;

public class DuplicateCustomerEmailException extends RuntimeException {

    public DuplicateCustomerEmailException(String message) {
        super(message);
    }
}