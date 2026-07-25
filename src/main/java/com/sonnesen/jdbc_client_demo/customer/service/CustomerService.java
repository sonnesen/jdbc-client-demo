package com.sonnesen.jdbc_client_demo.customer.service;

import com.sonnesen.jdbc_client_demo.common.dto.PageResponse;
import com.sonnesen.jdbc_client_demo.customer.dto.request.CreateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.request.UpdateCustomerRequest;
import com.sonnesen.jdbc_client_demo.customer.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CreateCustomerRequest customer);

    CustomerResponse findById(Long id);

    PageResponse<CustomerResponse> findAll(int page, int size);

    CustomerResponse update(Long id, UpdateCustomerRequest customer);

    void deleteById(Long id);
}
