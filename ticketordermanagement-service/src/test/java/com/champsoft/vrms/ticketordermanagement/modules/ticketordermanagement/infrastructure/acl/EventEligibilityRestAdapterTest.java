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

class EventEligibilityRestAdapterTest {

    private EventEligibilityRestAdapter adapter;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        adapter = new EventEligibilityRestAdapter(restTemplate);
        ReflectionTestUtils.setField(adapter, "baseUrl", "http://event-service");
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void adapterCallsExpectedUrlAndHandlesResponses() {
        server.expect(requestTo("http://event-service/api/events/event-1/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "eventId": "event-1",
                          "eligible": true
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThat(adapter.isEligible("event-1")).isTrue();
    }

    @Test
    void adapterPropagatesNotFound() {
        server.expect(requestTo("http://event-service/api/events/event-404/eligibility"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> adapter.isEligible("event-404")).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void adapterPropagatesServerErrors() {
        server.expect(requestTo("http://event-service/api/events/event-500/eligibility"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.isEligible("event-500")).isInstanceOf(Exception.class);
    }
}
