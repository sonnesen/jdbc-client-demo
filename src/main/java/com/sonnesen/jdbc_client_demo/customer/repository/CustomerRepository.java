package com.sonnesen.jdbc_client_demo.customer.repository;

import java.util.List;
import java.util.Optional;

import com.sonnesen.jdbc_client_demo.customer.model.Customer;

public interface CustomerRepository {

    List<Customer> findAll(int page, int size);

    long countAll();

    Optional<Customer> findById(Long id);

    Customer create(Customer customer);

    void update(Long id, Customer customer);

    void deleteById(Long id);
}
