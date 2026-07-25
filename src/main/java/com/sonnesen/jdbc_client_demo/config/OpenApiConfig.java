package com.sonnesen.jdbc_client_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("JDBC Client Demo API")
                        .description("CRUD REST API for the Customer entity, built with Spring Boot 4.1 and JdbcClient.")
                        .version("v1")
                        .contact(new Contact().name("sonnesen")));
    }
}
