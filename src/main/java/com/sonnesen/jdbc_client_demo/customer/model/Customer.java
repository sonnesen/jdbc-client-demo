package com.sonnesen.jdbc_client_demo.customer.model;

import java.util.Objects;

public record Customer(
        Long id,
        String name,
        String email) {

    public Customer {
        name = Objects.requireNonNull(name, "name must not be null").trim();
        email = Objects.requireNonNull(email, "email must not be null").trim();

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }

    public static Customer newCustomer(String name, String email) {
        return new Customer(null, name, email);
    }

    public Customer withId(Long id) {
        return new Customer(id, this.name, this.email);
    }
}
