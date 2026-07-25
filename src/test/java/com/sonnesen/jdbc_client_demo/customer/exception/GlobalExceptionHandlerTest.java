package com.sonnesen.jdbc_client_demo.customer.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/customers/99");
    }

    @Test
    void handleCustomerNotFoundException_returnsNotFoundProblemDetail() {
        CustomerNotFoundException ex = new CustomerNotFoundException(99L);

        ProblemDetail problemDetail = handler.handleCustomerNotFoundException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Customer Not Found");
        assertThat(problemDetail.getDetail()).isEqualTo(ex.getMessage());
        assertThat(problemDetail.getInstance()).hasToString("/v1/customers/99");
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleDataIntegrityViolationException_returnsConflictWithGenericMessage() {
        when(request.getMethod()).thenReturn("PUT");
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint \"customers_email_key\"");

        ProblemDetail problemDetail = handler.handleDataIntegrityViolationException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Data Integrity Violation");
        assertThat(problemDetail.getDetail())
                .isEqualTo("The request could not be completed because it violates a data integrity constraint.")
                .doesNotContain("customers_email_key");
    }

    @Test
    void handleCustomerAlreadyExistsWithEmailException_returnsConflictProblemDetail() {
        CustomerAlreadyExistsWithEmailException ex = new CustomerAlreadyExistsWithEmailException("jane@example.com");

        ProblemDetail problemDetail = handler.handleCustomerAlreadyExistsWithEmailException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getTitle()).isEqualTo("E-mail already exists");
        assertThat(problemDetail.getDetail()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequestWithFieldErrors() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("createCustomerRequest", "email", "must not be blank")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail problemDetail = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Validation Failed");
        assertThat(problemDetail.getProperties()).containsEntry("errors", List.of("email: must not be blank"));
    }

    @Test
    void handleConstraintViolationException_returnsBadRequestWithFieldErrors() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("findById.id");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than or equal to 1");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ProblemDetail problemDetail = handler.handleConstraintViolationException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Validation Failed");
        assertThat(problemDetail.getProperties()).containsEntry("errors",
                List.of("id: must be greater than or equal to 1"));
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequestProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("size must be positive");

        ProblemDetail problemDetail = handler.handleIllegalArgumentException(ex, request);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Invalid Argument");
        assertThat(problemDetail.getDetail()).isEqualTo("size must be positive");
    }
}
