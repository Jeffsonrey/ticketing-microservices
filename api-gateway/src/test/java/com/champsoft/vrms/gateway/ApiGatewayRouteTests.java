package com.champsoft.vrms.gateway;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayRouteTests {

	private static DisposableServer customersService;
	private static DisposableServer eventService;
	private static DisposableServer ticketInventoryService;
	private static DisposableServer ticketOrderService;

	@LocalServerPort
	private int port;

	@BeforeAll
	static void startStubServices() {
		customersService = startStubServer("customers-service");
		eventService = startStubServer("event-service");
		ticketInventoryService = startStubServer("ticketinventory-service");
		ticketOrderService = startStubServer("ticketordermanagement-service");
	}

	@AfterAll
	static void stopStubServices() {
		customersService.disposeNow();
		eventService.disposeNow();
		ticketInventoryService.disposeNow();
		ticketOrderService.disposeNow();
	}

	@Test
	void routesCustomersRequestsThroughGateway() {
		newWebTestClient().get()
				.uri("/api/customers/123")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("customers-service");
	}

	@Test
	void routesEventRequestsThroughGateway() {
		newWebTestClient().get()
				.uri("/api/events/123")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("event-service");
	}

	@Test
	void routesTicketInventoryRequestsThroughGateway() {
		newWebTestClient().get()
				.uri("/api/ticket-inventories/123")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("ticketinventory-service");
	}

	@Test
	void routesTicketOrderRequestsThroughGateway() {
		newWebTestClient().get()
				.uri("/api/ticket-orders/123")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("ticketordermanagement-service");
	}

	private static DisposableServer startStubServer(String responseBody) {
		return HttpServer.create()
				.host("localhost")
				.port(resolvePort(responseBody))
				.handle((request, response) -> response.sendString(Mono.just(responseBody)))
				.bindNow();
	}

	private WebTestClient newWebTestClient() {
		return WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	private static int resolvePort(String responseBody) {
		return switch (responseBody) {
			case "customers-service" -> 8081;
			case "event-service" -> 8082;
			case "ticketinventory-service" -> 8083;
			case "ticketordermanagement-service" -> 8084;
			default -> throw new IllegalArgumentException("Unknown stub service: " + responseBody);
		};
	}
}
