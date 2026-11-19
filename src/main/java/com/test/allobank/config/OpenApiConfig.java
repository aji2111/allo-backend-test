package com.test.allobank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI splitBillOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Allo Bank Split Bill API")
                                .description(
                                        "REST API for managing shared expenses and settlements"
                                )
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Wahyu Aji")
                                                .email("wasa7018@gmail.com")
                                )
                );
    }
}
