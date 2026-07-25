package com.sonnesen.jdbc_client_demo.customer.dto.response;

import com.sonnesen.jdbc_client_demo.customer.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

public record CustomerResponse(
        @Schema(description = "Customer id", example = "1") Long id,
        @Schema(description = "Customer name", example = "Ada Lovelace") String name,
        @Schema(description = "Customer e-mail", example = "ada@example.com") String email) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.id(), customer.name(), customer.email());
    }
}
