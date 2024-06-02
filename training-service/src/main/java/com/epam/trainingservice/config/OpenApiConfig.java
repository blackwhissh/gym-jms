package com.epam.trainingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("TRAINING-SERVICE")
                        .description("Part of gym-microservice")
                        .contact(new Contact().email("nikoloz_kiladze@epam.com")));
    }
}
