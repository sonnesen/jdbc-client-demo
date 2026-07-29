# jdbc-client-demo

A small Spring Boot 4.1 demo showcasing the `JdbcClient` API (the modern replacement for `JdbcTemplate`/`NamedParameterJdbcTemplate`) through a CRUD REST API for a `Customer` entity, backed by PostgreSQL.

## What it demonstrates

- **`JdbcClient`** for named-parameter SQL, record row-mapping (`.query(Customer.class)`), and generated-key retrieval on insert (`com.sonnesen.jdbc_client_demo.customer.repository.JdbcCustomerRepository`).
- **`ProblemDetail`** (RFC 7807) error responses via a centralized `@RestControllerAdvice` (`GlobalExceptionHandler`), covering not-found, validation, duplicate-email, and generic data-integrity failures.
- **Bean Validation** on request bodies, query parameters, and path variables (`@Valid`, `@Validated`, `@Min`/`@Max`).
- A unique constraint on `customers.email` (enforced at the DB level in `schema.sql`), translated from a `DuplicateKeyException` into a `409 Conflict` (`CustomerAlreadyExistsWithEmailException`) on both create and update.
- Layered architecture: `CustomerAPI` (interface carrying the OpenAPI/validation annotations) → `CustomerController` → service → repository, with DTOs separating the wire format from the domain model.

## Requirements

- Java 25
- No local Maven install needed — use the bundled wrapper (`./mvnw`).
- Docker (running locally) — used both by `spring-boot-docker-compose` to start Postgres for `spring-boot:run`, and by Testcontainers to spin up a real Postgres instance for the test suite.

## Running the app

```
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`.

## Running the tests

```
./mvnw test
```

Covers unit tests (`CustomerServiceImplTest`, `GlobalExceptionHandlerTest`), a JDBC slice test against a real Postgres container (`JdbcCustomerRepositoryTest`, `@JdbcTest`), a web-layer slice test (`CustomerControllerTest`, `@WebMvcTest`), and a context-load smoke test (`JdbcClientDemoApplicationTests`).

Integration tests provision Postgres via [Testcontainers](https://testcontainers.com/) (`TestcontainersConfiguration`, `@ServiceConnection`) — schema and seed data come from `src/main/resources/db/postgres/`, the same scripts used in production, so tests run against the real database engine and constraints.

## Endpoints

Base path: `/v1/customers`. See `requests.http` for ready-to-run sample requests.

| Method | Path         | Body                        | Notes                                                                 |
|--------|--------------|------------------------------|------------------------------------------------------------------------|
| GET    | `/`          | —                            | Query params `page` (default 1, `>=1`) and `size` (default 10, `1-100`). Returns a paginated envelope: `{ content, page, size, totalElements, totalPages }`. |
| GET    | `/{id}`      | —                            | `id` must be `>= 1`. 404 if not found.                                 |
| POST   | `/`          | `{ "name", "email" }`        | 201 on success. 409 if the email already exists.                       |
| PUT    | `/{id}`      | `{ "name", "email" }`        | `id` must be `>= 1`. 404 if not found. 409 if the email already belongs to another customer. |
| DELETE | `/{id}`      | —                            | `id` must be `>= 1`. 204 on success, 404 if not found.                  |

Validation failures (blank/invalid fields, out-of-range `id`/`page`/`size`) return `400` with a `ProblemDetail` body listing the offending fields under `errors`.

## API documentation (OpenAPI / Swagger UI)

With the app running, the API is documented using [springdoc-openapi](https://springdoc.org/):

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Raw OpenAPI spec (JSON): `http://localhost:8080/v3/api-docs`

## Project structure

```
customer/
├── controller/  CustomerAPI (endpoint contract + OpenAPI annotations), CustomerController (implementation)
├── dto/         Request/response records (request/, response/)
├── exception/   CustomerNotFoundException, CustomerAlreadyExistsWithEmailException, GlobalExceptionHandler
├── model/       Customer domain record
├── repository/  CustomerRepository interface + JdbcClient-based implementation
└── service/     CustomerService interface + implementation
common/
└── dto/         Shared PageResponse<T> envelope
config/
└── OpenApiConfig  Swagger UI / OpenAPI metadata (title, description, contact)
```
