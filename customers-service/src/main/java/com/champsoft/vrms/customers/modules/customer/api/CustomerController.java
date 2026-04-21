package com.champsoft.vrms.customers.modules.customer.api;

import com.champsoft.vrms.customers.modules.customer.api.dto.CustomerRequestModel;
import com.champsoft.vrms.customers.modules.customer.api.dto.CustomerResponseModel;

import com.champsoft.vrms.customers.modules.customer.api.mapper.CustomerResponseMapper;
import com.champsoft.vrms.customers.modules.customer.application.service.CustomerService;
import com.champsoft.vrms.customers.modules.customer.model.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseModel>> getAllCustomers() {
        List<CustomerResponseModel> response = customerService.getAllCustomers()
                .stream()
                .map(CustomerResponseMapper::toResponseModel)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseModel> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(CustomerResponseMapper.toResponseModel(customer));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseModel> createCustomer(
            @Valid @RequestBody CustomerRequestModel requestModel) {

        Customer createdCustomer = customerService.createCustomer(requestModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerResponseMapper.toResponseModel(createdCustomer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseModel> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestModel requestModel) {

        Customer updatedCustomer = customerService.updateCustomer(id, requestModel);
        return ResponseEntity.ok(CustomerResponseMapper.toResponseModel(updatedCustomer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}