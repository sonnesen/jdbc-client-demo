package com.sonnesen.jdbc_client_demo.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer with id " + id + " not found", null, true, false);
    }

}
