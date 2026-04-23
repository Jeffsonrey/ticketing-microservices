package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    GroupedOpenApi carsApi() {
        return GroupedOpenApi.builder().group("cars").pathsToMatch("/api/cars/**").build();
    }

    @Bean
    GroupedOpenApi ownersApi() {
        return GroupedOpenApi.builder().group("owners").pathsToMatch("/api/owners/**").build();
    }

    @Bean
    GroupedOpenApi agentsApi() {
        return GroupedOpenApi.builder().group("agents").pathsToMatch("/api/agents/**").build();
    }

    @Bean
    GroupedOpenApi registrationApi() {
        return GroupedOpenApi.builder().group("registration").pathsToMatch("/api/registrations/**").build();
    }
}
