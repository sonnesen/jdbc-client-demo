package com.sonnesen.jdbc_client_demo.customer.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sonnesen.jdbc_client_demo.TestcontainersConfiguration;
import com.sonnesen.jdbc_client_demo.customer.exception.CustomerAlreadyExistsWithEmailException;
import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.model.Customer;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ JdbcCustomerRepository.class, TestcontainersConfiguration.class })
@ActiveProfiles("test")
class JdbcCustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        customerRepository.findAll(1, Integer.MAX_VALUE).forEach(customer -> customerRepository.deleteById(customer.id()));
        // Insert a sample customer for testing
        customerRepository.create(Customer.newCustomer("John Doe", "john.doe@mail.com"));
    }

    @Test
    void create_generatesIdAndPersistsCustomer() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));

        assertThat(created.id()).isNotNull();
        assertThat(customerRepository.findById(created.id())).contains(created);
    }

    @Test
    void create_throwsCustomerAlreadyExistsWithEmailException_onDuplicateEmail() {
        Customer duplicate = Customer.newCustomer("Duplicate", "john.doe@mail.com");
        assertThatThrownBy(() -> customerRepository.create(duplicate))
                .isInstanceOf(CustomerAlreadyExistsWithEmailException.class);
    }

    @Test
    void findById_returnsEmpty_whenMissing() {
        Optional<Customer> found = customerRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_respectsPageAndSize() {
        customerRepository.create(Customer.newCustomer("Customer Two", "two@example.com"));
        customerRepository.create(Customer.newCustomer("Customer Three", "three@example.com"));

        List<Customer> firstPage = customerRepository.findAll(1, 2);
        List<Customer> secondPage = customerRepository.findAll(2, 2);

        assertThat(firstPage).hasSize(2);
        assertThat(secondPage).hasSize(1);
    }

    @Test
    void countAll_returnsTotalRowCount() {
        customerRepository.create(Customer.newCustomer("Customer Two", "two@example.com"));

        assertThat(customerRepository.countAll()).isEqualTo(2L);
    }

    @Test
    void update_changesNameAndEmail() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));

        customerRepository.update(created.id(), new Customer(created.id(), "Jane Updated", "jane.updated@example.com"));

        Optional<Customer> updated = customerRepository.findById(created.id());
        assertThat(updated).contains(new Customer(created.id(), "Jane Updated", "jane.updated@example.com"));
    }

    @Test
    void update_throwsCustomerNotFoundException_whenMissing() {
        Customer customer = new Customer(999L, "Ghost", "ghost@example.com");
        assertThatThrownBy(() -> customerRepository.update(999L, customer))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void update_throwsCustomerAlreadyExistsWithEmailException_onDuplicateEmail() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));
        Customer updateCustomer = new Customer(created.id(), "Jane Doe", "john.doe@mail.com");
        long customerId = created.id();

        assertThatThrownBy(() -> customerRepository.update(customerId, updateCustomer))
                .isInstanceOf(CustomerAlreadyExistsWithEmailException.class);
    }

    @Test
    void deleteById_removesCustomer() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));
        long customerId = created.id();

        customerRepository.deleteById(customerId);

        assertThat(customerRepository.findById(customerId)).isEmpty();
    }

    @Test
    void deleteById_throwsCustomerNotFoundException_whenMissing() {
        long missingId = 999L;
        assertThatThrownBy(() -> customerRepository.deleteById(missingId))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
