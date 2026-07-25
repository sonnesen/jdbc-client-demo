package com.sonnesen.jdbc_client_demo.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sonnesen.jdbc_client_demo.common.dto.PageResponse;
import com.sonnesen.jdbc_client_demo.customer.dto.request.CreateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.request.UpdateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.response.CustomerResponse;
import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.model.Customer;
import com.sonnesen.jdbc_client_demo.customer.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void create_delegatesToRepositoryAndMapsResponse() {
        CreateCustomerRequest request = new CreateCustomerRequest("Jane Doe", "jane.doe@example.com");
        Customer saved = new Customer(1L, "Jane Doe", "jane.doe@example.com");
        when(customerRepository.create(any(Customer.class))).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertThat(response).isEqualTo(new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com"));
    }

    @Test
    void findById_returnsMappedResponse_whenFound() {
        Customer customer = new Customer(1L, "Jane Doe", "jane.doe@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.findById(1L);

        assertThat(response).isEqualTo(new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com"));
    }

    @Test
    void findById_throwsCustomerNotFoundException_whenMissing() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void findAll_mapsContentAndComputesPageMetadata() {
        List<Customer> customers = List.of(
                new Customer(1L, "Jane Doe", "jane.doe@example.com"),
                new Customer(2L, "John Smith", "john.smith@mail.com"));
        when(customerRepository.findAll(1, 2)).thenReturn(customers);
        when(customerRepository.countAll()).thenReturn(5L);

        PageResponse<CustomerResponse> page = customerService.findAll(1, 2);

        assertThat(page.content()).containsExactly(
                new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com"),
                new CustomerResponse(2L, "John Smith", "john.smith@mail.com"));
        assertThat(page.page()).isEqualTo(1);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(5L);
        assertThat(page.totalPages()).isEqualTo(3);
    }

    @Test
    void update_delegatesToRepository() {
        UpdateCustomerRequest request = new UpdateCustomerRequest("Jane Doe Updated", "jane.updated@example.com");

        CustomerResponse response = customerService.update(1L, request);

        verify(customerRepository).update(1L, request.toModel(1L));
        assertThat(response).isEqualTo(new CustomerResponse(1L, "Jane Doe Updated", "jane.updated@example.com"));
    }

    @Test
    void deleteById_delegatesToRepository() {
        customerService.deleteById(1L);

        verify(customerRepository).deleteById(1L);
    }
}
