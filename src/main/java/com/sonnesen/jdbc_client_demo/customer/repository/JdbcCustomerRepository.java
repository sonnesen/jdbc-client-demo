package com.sonnesen.jdbc_client_demo.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.sonnesen.jdbc_client_demo.customer.exception.CustomerNotFoundException;
import com.sonnesen.jdbc_client_demo.customer.model.Customer;

@Repository
@Transactional
public class JdbcCustomerRepository implements CustomerRepository {

    private final JdbcClient jdbcClient;

    public JdbcCustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Customer> findAll(int page, int size) {
        String sql = "SELECT * FROM customers ORDER BY id LIMIT :limit OFFSET :offset";
        int offset = (page - 1) * size;
        return jdbcClient.sql(sql)
                .param("limit", size)
                .param("offset", offset)
                .query(Customer.class)
                .list();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = :id";
        return jdbcClient.sql(sql).param("id", id).query(Customer.class).optional();
    }

    @Override
    public Customer create(Customer customer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO customers (name, email) VALUES (:name, :email)";
        int update = jdbcClient.sql(sql)
                .param("name", customer.name())
                .param("email", customer.email())
                .update(keyHolder);

        Assert.state(update > 0, "Failed to create customer");

        Long generatedId = keyHolder.getKey().longValue();
        customer = customer.withId(generatedId);

        return customer;
    }

    @Override
    public void update(Long id, Customer customer) {
        String sql = "UPDATE customers SET name = :name, email = :email WHERE id = :id";
        int update = jdbcClient.sql(sql)
                .param("name", customer.name())
                .param("email", customer.email())
                .param("id", id)
                .update();

        if (update == 0) {
            throw new CustomerNotFoundException(id);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM customers WHERE id = :id";
        int deleteById = jdbcClient.sql(sql).param("id", id).update();

        if (deleteById == 0) {
            throw new CustomerNotFoundException(id);
        }
    }

}
