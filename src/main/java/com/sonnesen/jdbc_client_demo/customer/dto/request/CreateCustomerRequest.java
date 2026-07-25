package com.sonnesen.jdbc_client_demo.customer.dto.request;

import com.sonnesen.jdbc_client_demo.customer.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @Schema(description = "Customer name", example = "Ada Lovelace") @NotBlank @Size(max = 255) String name,
        @Schema(description = "Customer e-mail, must be unique", example = "ada@example.com") @NotBlank @Email @Size(max = 255) String email) {

    public Customer toModel() {
        return Customer.newCustomer(this.name(), this.email());
    }
}
