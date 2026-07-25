package com.sonnesen.jdbc_client_demo.customer.dto.request;

import com.sonnesen.jdbc_client_demo.customer.model.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Email @Size(max = 255) String email) {

    public Customer toModel(Long id) {
        return Customer.newCustomer(this.name(), this.email()).withId(id);
    }
}
