package com.champsoft.vrms.customers.modules.customer.api.mapper;

import com.champsoft.vrms.customers.modules.customer.api.dto.CustomerResponseModel;
import com.champsoft.vrms.customers.modules.customer.model.Customer;

public class CustomerResponseMapper {

    private CustomerResponseMapper() {
    }

    public static CustomerResponseModel toResponseModel(Customer customer) {
        return new CustomerResponseModel(
                customer.getId(),
                customer.getCustomerIdentifier(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmailAddress(),
                customer.getPhoneNumber()
        );
    }
}