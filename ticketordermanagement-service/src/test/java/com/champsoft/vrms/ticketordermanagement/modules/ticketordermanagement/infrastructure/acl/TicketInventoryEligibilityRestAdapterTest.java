package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.acl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TicketInventoryEligibilityRestAdapterTest {

    private TicketInventoryEligibilityRestAdapter adapter;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        adapter = new TicketInventoryEligibilityRestAdapter(restTemplate);
        ReflectionTestUtils.setField(adapter, "baseUrl", "http://inventory-service");
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void adapterCallsExpectedUrlAndHandlesResponses() {
        server.expect(requestTo("http://inventory-service/api/ticket-inventories/eligibility?eventId=event-1&ticketType=VIP&quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertThat(adapter.isEligible("event-1", "VIP", 2)).isTrue();
    }

    @Test
    void adapterPropagatesNotFound() {
        server.expect(requestTo("http://inventory-service/api/ticket-inventories/eligibility?eventId=missing&ticketType=VIP&quantity=1"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> adapter.isEligible("missing", "VIP", 1)).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void adapterPropagatesServerErrors() {
        server.expect(requestTo("http://inventory-service/api/ticket-inventories/eligibility?eventId=event-500&ticketType=VIP&quantity=1"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.isEligible("event-500", "VIP", 1)).isInstanceOf(Exception.class);
    }
}
