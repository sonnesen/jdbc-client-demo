CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT customers_pk PRIMARY KEY (id),
    CONSTRAINT customers_email_unique UNIQUE (email)
);