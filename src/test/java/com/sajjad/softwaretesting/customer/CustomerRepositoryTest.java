package com.sajjad.softwaretesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
                /*This line is needed for our validation to work in here*/
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;


    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "09000";
        Customer customer = new Customer(id, "Reza", phoneNumber);
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> customerOptional = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).usingRecursiveComparison().isEqualTo(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberDoesNotExist() {
        // Given
        String phoneNumber = "09000";
        // When
        Optional<Customer> customerOptional = underTest.selectCustomerByPhoneNumber(phoneNumber);
        // Then
        assertThat(customerOptional).isNotPresent();

    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Reza", "09000");
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);

        assertThat(optionalCustomer).
                isPresent().
                hasValueSatisfying(c -> {
//                  assertThat(customer.getId()).isEqualTo(customer.getId());
//                  assertThat(customer.getName()).isEqualTo(customer.getName());
//                  assertThat(customer.getName()).isEqualTo(customer.getName());
//                  assertThat(customer.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
                    assertThat(c).usingRecursiveComparison().isEqualTo(customer);
                });

    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "09000");
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.sajjad.softwaretesting.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Reza", null);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.sajjad.softwaretesting.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}