package com.sonnesen.jdbc_client_demo.customer.dto.response;

import com.sonnesen.jdbc_client_demo.customer.model.Customer;

public record CustomerResponse(
        Long id,
        String name,
        String email) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.id(), customer.name(), customer.email());
    }
}
