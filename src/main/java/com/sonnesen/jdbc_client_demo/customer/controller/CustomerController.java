package com.sonnesen.jdbc_client_demo.customer.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sonnesen.jdbc_client_demo.common.dto.PageResponse;
import com.sonnesen.jdbc_client_demo.customer.dto.request.CreateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.request.UpdateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.response.CustomerResponse;
import com.sonnesen.jdbc_client_demo.customer.service.CustomerService;

@RestController
public class CustomerController implements CustomerAPI {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public PageResponse<CustomerResponse> findAll(int page, int size) {
        return customerService.findAll(page, size);
    }

    @Override
    public CustomerResponse findById(Long id) {
        return customerService.findById(id);
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        return customerService.create(request);
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        return customerService.update(id, request);
    }

    @Override
    public void deleteById(Long id) {
        customerService.deleteById(id);
    }

}
