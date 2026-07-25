package com.sonnesen.jdbc_client_demo.customer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.sonnesen.jdbc_client_demo.common.dto.PageResponse;
import com.sonnesen.jdbc_client_demo.customer.dto.response.CustomerResponse;
import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.service.CustomerService;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void findAll_returnsPagedResponse() {
        CustomerResponse customer = new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com");
        when(customerService.findAll(anyInt(), anyInt()))
                .thenReturn(PageResponse.of(List.of(customer), 1, 10, 1L));

        assertThat(mvc.get().uri("/v1/customers"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content")
                .asArray()
                .hasSize(1);
    }

    @Test
    void findById_returnsCustomer_whenFound() {
        CustomerResponse customer = new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com");
        when(customerService.findById(1L)).thenReturn(customer);

        assertThat(mvc.get().uri("/v1/customers/1"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.email")
                .isEqualTo("jane.doe@example.com");
    }

    @Test
    void findById_returnsNotFound_whenMissing() {
        when(customerService.findById(999L)).thenThrow(new CustomerNotFoundException(999L));

        assertThat(mvc.get().uri("/v1/customers/999")).hasStatus(404);
    }

    @Test
    void findById_returnsBadRequest_whenIdIsNotPositive() {
        assertThat(mvc.get().uri("/v1/customers/0")).hasStatus(400);
    }

    @Test
    void create_returnsCreated_onValidRequest() {
        CustomerResponse created = new CustomerResponse(1L, "Jane Doe", "jane.doe@example.com");
        when(customerService.create(any())).thenReturn(created);

        assertThat(mvc.post().uri("/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Jane Doe", "email": "jane.doe@example.com"}
                        """))
                .hasStatus(201);
    }

    @Test
    void create_returnsBadRequest_whenNameIsBlank() {
        assertThat(mvc.post().uri("/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "", "email": "jane.doe@example.com"}
                        """))
                .hasStatus(400);
    }

    @Test
    void create_returnsBadRequest_whenEmailIsInvalid() {
        assertThat(mvc.post().uri("/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Jane Doe", "email": "not-an-email"}
                        """))
                .hasStatus(400);
    }

    @Test
    void update_returnsOk_onValidRequest() {
        CustomerResponse updated = new CustomerResponse(1L, "Jane Updated", "jane.updated@example.com");
        when(customerService.update(eq(1L), any())).thenReturn(updated);

        assertThat(mvc.put().uri("/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Jane Updated", "email": "jane.updated@example.com"}
                        """))
                .hasStatusOk();
    }

    @Test
    void update_returnsBadRequest_whenIdIsNotPositive() {
        assertThat(mvc.put().uri("/v1/customers/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Jane Updated", "email": "jane.updated@example.com"}
                        """))
                .hasStatus(400);
    }

    @Test
    void deleteById_returnsNoContent_whenFound() {
        assertThat(mvc.delete().uri("/v1/customers/1")).hasStatus(204);
    }

    @Test
    void deleteById_returnsNotFound_whenMissing() {
        doThrow(new CustomerNotFoundException(999L))
                .when(customerService).deleteById(999L);

        assertThat(mvc.delete().uri("/v1/customers/999")).hasStatus(404);
    }

    @Test
    void findAll_returnsBadRequest_whenPageIsZero() {
        assertThat(mvc.get().uri("/v1/customers").param("page", "0")).hasStatus(400);
    }

    @Test
    void findAll_returnsBadRequest_whenSizeExceedsMax() {
        assertThat(mvc.get().uri("/v1/customers").param("size", "101")).hasStatus(400);
    }
}
