package com.champsoft.vrms.customers.modules.customer.application.service;

import com.champsoft.vrms.customers.modules.customer.api.dto.CustomerRequestModel;
import com.champsoft.vrms.customers.modules.customer.application.exception.CustomerNotFoundException;
import com.champsoft.vrms.customers.modules.customer.application.exception.DuplicateCustomerEmailException;
import com.champsoft.vrms.customers.modules.customer.application.port.out.CustomerRepositoryPort;
import com.champsoft.vrms.customers.modules.customer.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepositoryPort customerRepositoryPort;

    public CustomerService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    public List<Customer> getAllCustomers() {
        return customerRepositoryPort.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepositoryPort.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id
                ));
    }

    public Customer createCustomer(CustomerRequestModel requestModel) {
        validateDuplicateEmail(requestModel.getEmailAddress(), null);

        Customer customer = new Customer(
                requestModel.getFirstName(),
                requestModel.getLastName(),
                requestModel.getEmailAddress(),
                requestModel.getPhoneNumber()
        );

        return customerRepositoryPort.save(customer);
    }

    public Customer updateCustomer(Long id, CustomerRequestModel requestModel) {
        Customer existingCustomer = getCustomerById(id);

        validateDuplicateEmail(requestModel.getEmailAddress(), id);

        existingCustomer.setFirstName(requestModel.getFirstName());
        existingCustomer.setLastName(requestModel.getLastName());
        existingCustomer.setEmailAddress(requestModel.getEmailAddress());
        existingCustomer.setPhoneNumber(requestModel.getPhoneNumber());

        return customerRepositoryPort.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer existingCustomer = getCustomerById(id);
        customerRepositoryPort.delete(existingCustomer);
    }

    private void validateDuplicateEmail(String emailAddress, Long currentCustomerId) {
        customerRepositoryPort.findByEmailAddress(emailAddress).ifPresent(existingCustomer -> {
            if (currentCustomerId == null || !existingCustomer.getId().equals(currentCustomerId)) {
                throw new DuplicateCustomerEmailException(
                        "A customer with this email already exists."
                );
            }
        });
    }
}