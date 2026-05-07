package com.champsoft.vrms.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void routeLocatorContainsExpectedRouteIdsAndUris() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        assertThat(routes).isNotNull();
        assertThat(routes).extracting(Route::getId)
                .contains("customers-route", "events-route", "ticket-inventories-route", "ticket-orders-route");
        assertThat(routes).extracting(route -> route.getUri().toString())
                .contains("http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084");
    }

    @Test
    void gatewayHandlesUnknownRoutesAndHealthEndpoint() {
        webTestClient.get()
                .uri("/does-not-exist")
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}
