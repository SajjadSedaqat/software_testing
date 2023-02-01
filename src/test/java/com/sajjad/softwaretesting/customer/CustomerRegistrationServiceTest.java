package com.sajjad.softwaretesting.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    private CustomerRegistrationService underTest;
    private AutoCloseable closeable;
    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }


    @Test
    void itShouldSaveNewCustomer() {
        // Given
        String phoneNumber = "09000";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison().isEqualTo(customer);

    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerAlreadyExist() {
        // Given
        String phoneNumber = "09000";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should(never()).save(any());
//        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
//        then(customerRepository).shouldHaveNoMoreInteractions();


    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given
        String phoneNumber = "09000";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "Ali", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer2));

        // When
        // Then
        assertThatThrownBy(() -> {
            underTest.registerNewCustomer(request);
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("phone number [%s] is taken", phoneNumber);

        // Finally
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given
        String phoneNumber = "09000";
        Customer customer = new Customer(null, "Maryam", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(customer);
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }
}