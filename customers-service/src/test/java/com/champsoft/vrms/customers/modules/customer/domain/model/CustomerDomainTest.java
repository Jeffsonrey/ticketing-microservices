package com.champsoft.vrms.customers.modules.customer.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDomainTest {

    @Test
    void customerGeneratesIdentifierAndIsEligibleWhenPhoneHasEnoughDigits() {
        Customer customer = new Customer("Jane", "Doe", "jane@example.com", "(514) 555-1212");

        assertThat(customer.getCustomerIdentifier()).isNotBlank();
        assertThat(customer.isEligibleForRegistration()).isTrue();
    }

    @Test
    void customerIsNotEligibleWhenPhoneIsTooShort() {
        Customer customer = new Customer("Sam", "Short", "sam@example.com", "123");

        assertThat(customer.isEligibleForRegistration()).isFalse();
    }

    @Test
    void customerIsNotEligibleWhenEmailOrPhoneAreMissing() {
        Customer missingEmail = new Customer("No", "Email", null, "5145551212");
        Customer invalidEmail = new Customer("Bad", "Email", "bad-email", "5145551212");
        Customer missingPhone = new Customer("No", "Phone", "good@example.com", null);

        assertThat(missingEmail.isEligibleForRegistration()).isFalse();
        assertThat(invalidEmail.isEligibleForRegistration()).isFalse();
        assertThat(missingPhone.isEligibleForRegistration()).isFalse();
    }

    @Test
    void customerIdFactoryReturnsRecordValue() {
        CustomerId customerId = CustomerId.of(42L);

        assertThat(customerId.value()).isEqualTo(42L);
    }
}
