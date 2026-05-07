package com.champsoft.vrms.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    RouteLocator gatewayRoutes(
            RouteLocatorBuilder builder,
            @Value("${services.customers.uri}") String customersUri,
            @Value("${services.events.uri}") String eventsUri,
            @Value("${services.ticket-inventories.uri}") String ticketInventoriesUri,
            @Value("${services.ticket-orders.uri}") String ticketOrdersUri
    ) {
        return builder.routes()
                .route("customers-route", route -> route.path("/api/customers/**").uri(customersUri))
                .route("events-route", route -> route.path("/api/events/**").uri(eventsUri))
                .route("ticket-inventories-route", route -> route.path("/api/ticket-inventories/**").uri(ticketInventoriesUri))
                .route("ticket-orders-route", route -> route.path("/api/ticket-orders/**").uri(ticketOrdersUri))
                .build();
    }
}
