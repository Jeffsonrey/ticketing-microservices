package com.champsoft.vrms.customers.modules.customer.application.service;

import com.champsoft.vrms.customers.modules.customer.api.dto.CustomerRequestModel;
import com.champsoft.vrms.customers.modules.customer.application.exception.CustomerNotFoundException;
import com.champsoft.vrms.customers.modules.customer.application.exception.DuplicateCustomerEmailException;
import com.champsoft.vrms.customers.modules.customer.application.port.out.CustomerRepositoryPort;
import com.champsoft.vrms.customers.modules.customer.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort repository;

    @InjectMocks
    private CustomerService service;

    @Test
    void createCustomerSavesNewCustomer() {
        CustomerRequestModel request = request("Jane", "Doe", "jane@example.com", "5145551212");
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = service.createCustomer(request);

        assertThat(saved.getEmailAddress()).isEqualTo("jane@example.com");
        verify(repository).save(any(Customer.class));
    }

    @Test
    void createCustomerRejectsDuplicateEmail() {
        Customer existing = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        when(repository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.createCustomer(request("Jane", "Doe", "jane@example.com", "5145551212")))
                .isInstanceOf(DuplicateCustomerEmailException.class);
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomerById(99L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void updateCustomerSavesMutatedCustomerWhenEmailIsUnique() {
        Customer existing = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        setId(existing, 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByEmailAddress("janet@example.com")).thenReturn(Optional.empty());
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = service.updateCustomer(1L, request("Janet", "Doe", "janet@example.com", "4385559898"));

        assertThat(updated.getFirstName()).isEqualTo("Janet");
        assertThat(updated.getEmailAddress()).isEqualTo("janet@example.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("4385559898");
    }

    @Test
    void updateCustomerRejectsDuplicateEmailFromAnotherCustomer() {
        Customer existing = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        setId(existing, 1L);
        Customer duplicate = new Customer("Other", "Person", "dupe@example.com", "5145553434");
        setId(duplicate, 2L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByEmailAddress("dupe@example.com")).thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> service.updateCustomer(1L, request("Jane", "Doe", "dupe@example.com", "5145551212")))
                .isInstanceOf(DuplicateCustomerEmailException.class);
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomerAllowsKeepingSameEmailForCurrentCustomer() {
        Customer existing = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        setId(existing, 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = service.updateCustomer(1L, request("Jane", "Updated", "jane@example.com", "5145550000"));

        assertThat(updated.getLastName()).isEqualTo("Updated");
        assertThat(updated.getEmailAddress()).isEqualTo("jane@example.com");
    }

    @Test
    void updateAndDeleteThrowWhenCustomerIsMissing() {
        when(repository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCustomer(77L, request("Jane", "Doe", "jane@example.com", "5145551212")))
                .isInstanceOf(CustomerNotFoundException.class);
        assertThatThrownBy(() -> service.deleteCustomer(77L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void listAndDeleteDelegateToRepository() {
        Customer customer = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        when(repository.findAll()).thenReturn(List.of(customer));
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        assertThat(service.getAllCustomers()).containsExactly(customer);

        service.deleteCustomer(1L);

        verify(repository).delete(customer);
    }

    @Test
    void eligibilityServiceHandlesTrueFalseAndMissing() {
        CustomerEligibilityService eligibilityService = new CustomerEligibilityService(repository);
        Customer eligible = new Customer("Jane", "Doe", "jane@example.com", "5145551212");
        Customer ineligible = new Customer("Jane", "Doe", "jane@example.com", "123");

        when(repository.findById(1L)).thenReturn(Optional.of(eligible));
        when(repository.findById(2L)).thenReturn(Optional.of(ineligible));
        when(repository.findById(3L)).thenReturn(Optional.empty());

        assertThat(eligibilityService.isEligible(1L)).isTrue();
        assertThat(eligibilityService.isEligible(2L)).isFalse();
        assertThatThrownBy(() -> eligibilityService.isEligible(3L)).isInstanceOf(CustomerNotFoundException.class);
    }

    private CustomerRequestModel request(String firstName, String lastName, String email, String phone) {
        CustomerRequestModel request = new CustomerRequestModel();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmailAddress(email);
        request.setPhoneNumber(phone);
        return request;
    }

    private void setId(Customer customer, Long id) {
        try {
            var field = Customer.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(customer, id);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
