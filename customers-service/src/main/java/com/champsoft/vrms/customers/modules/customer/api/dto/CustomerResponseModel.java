package com.champsoft.vrms.customers.modules.customer.api.dto;

public class CustomerResponseModel {

    private Long id;
    private String customerIdentifier;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

    public CustomerResponseModel(Long id, String customerIdentifier, String firstName,
                                 String lastName, String emailAddress, String phoneNumber) {
        this.id = id;
        this.customerIdentifier = customerIdentifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}