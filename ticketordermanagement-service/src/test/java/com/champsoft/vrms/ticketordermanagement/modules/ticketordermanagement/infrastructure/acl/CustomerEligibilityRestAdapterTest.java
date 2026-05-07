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

class CustomerEligibilityRestAdapterTest {

    private CustomerEligibilityRestAdapter adapter;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        adapter = new CustomerEligibilityRestAdapter(restTemplate);
        ReflectionTestUtils.setField(adapter, "baseUrl", "http://customers-service");
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void adapterCallsExpectedUrlAndHandlesResponses() {
        server.expect(requestTo("http://customers-service/api/customers/1/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertThat(adapter.isEligible("1")).isTrue();
    }

    @Test
    void adapterPropagatesNotFound() {
        server.expect(requestTo("http://customers-service/api/customers/2/eligibility"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> adapter.isEligible("2")).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void adapterPropagatesServerErrors() {
        server.expect(requestTo("http://customers-service/api/customers/3/eligibility"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.isEligible("3")).isInstanceOf(Exception.class);
    }
}
