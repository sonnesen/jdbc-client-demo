package com.sonnesen.jdbc_client_demo.customer.exception;

/**
 * CustomerAlreadyExistsWithEmailException
 */
public class CustomerAlreadyExistsWithEmailException extends RuntimeException {

    public CustomerAlreadyExistsWithEmailException(String email) {
        super("Customer with email " + email + " already exists", null, true, false);
    }

}
