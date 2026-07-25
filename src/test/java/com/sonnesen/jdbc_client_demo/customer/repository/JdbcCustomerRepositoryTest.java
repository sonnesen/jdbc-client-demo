package com.sonnesen.jdbc_client_demo.customer.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.model.Customer;

@JdbcTest
@Import(JdbcCustomerRepository.class)
class JdbcCustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void create_generatesIdAndPersistsCustomer() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));

        assertThat(created.id()).isNotNull();
        assertThat(customerRepository.findById(created.id())).contains(created);
    }

    @Test
    void create_throwsDataIntegrityViolationException_onDuplicateEmail() {
        assertThatThrownBy(() -> customerRepository.create(Customer.newCustomer("Duplicate", "john.doe@mail.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
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
        assertThatThrownBy(() -> customerRepository.update(999L, new Customer(999L, "Ghost", "ghost@example.com")))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void deleteById_removesCustomer() {
        Customer created = customerRepository.create(Customer.newCustomer("Jane Doe", "jane.doe@example.com"));

        customerRepository.deleteById(created.id());

        assertThat(customerRepository.findById(created.id())).isEmpty();
    }

    @Test
    void deleteById_throwsCustomerNotFoundException_whenMissing() {
        assertThatThrownBy(() -> customerRepository.deleteById(999L))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
