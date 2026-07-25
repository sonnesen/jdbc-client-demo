package com.sonnesen.jdbc_client_demo.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sonnesen.jdbc_client_demo.customer.dto.request.CreateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.request.UpdateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.response.CustomerResponse;
import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.model.Customer;
import com.sonnesen.jdbc_client_demo.customer.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        Customer customer = request.toModel();
        customer = customerRepository.create(customer);
        return CustomerResponse.from(customer);
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<CustomerResponse> findAll(int page, int size) {
        List<Customer> customers = customerRepository.findAll(page, size);
        return customers.stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Override
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = request.toModel(id);
        customerRepository.update(id, customer);
        return CustomerResponse.from(customer);
    }

}
