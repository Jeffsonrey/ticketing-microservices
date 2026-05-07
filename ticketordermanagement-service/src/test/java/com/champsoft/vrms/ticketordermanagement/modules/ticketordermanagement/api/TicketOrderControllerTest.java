package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.api;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.CustomerEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.EventEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketInventoryEligibilityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
@Import(TicketOrderControllerTest.MockPortsConfig.class)
class TicketOrderControllerTest {

    @TestConfiguration
    static class MockPortsConfig {
        @Bean
        CustomerEligibilityPort customerEligibilityPort() {
            return Mockito.mock(CustomerEligibilityPort.class);
        }

        @Bean
        EventEligibilityPort eventEligibilityPort() {
            return Mockito.mock(EventEligibilityPort.class);
        }

        @Bean
        TicketInventoryEligibilityPort ticketInventoryEligibilityPort() {
            return Mockito.mock(TicketInventoryEligibilityPort.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerEligibilityPort customerEligibilityPort;

    @Autowired
    private EventEligibilityPort eventEligibilityPort;

    @Autowired
    private TicketInventoryEligibilityPort ticketInventoryEligibilityPort;

    @BeforeEach
    void setUp() {
        Mockito.reset(customerEligibilityPort, eventEligibilityPort, ticketInventoryEligibilityPort);
        Mockito.when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        Mockito.when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        Mockito.when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2)).thenReturn(true);
        Mockito.when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 3)).thenReturn(true);
    }

    @Test
    void orderHappyPathFlowWorks() throws Exception {
        String response = mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = extractLongField(response, "id");

        mockMvc.perform(get("/api/ticket-orders/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/ticket-orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 3,
                                  "totalPrice": 149.99,
                                  "status": "CONFIRMED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(3));

        mockMvc.perform(delete("/api/ticket-orders/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ticket-orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void orderNegativePathsReturnExpectedStatuses() throws Exception {
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 0,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 0,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.when(customerEligibilityPort.isEligible("1")).thenReturn(false);
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.when(customerEligibilityPort.isEligible("1")).thenReturn(true);
        Mockito.when(eventEligibilityPort.isEligible("event-1")).thenReturn(false);
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.when(eventEligibilityPort.isEligible("event-1")).thenReturn(true);
        Mockito.when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2)).thenReturn(false);
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2)).thenReturn(true);
        Mockito.when(customerEligibilityPort.isEligible("1")).thenThrow(new RestClientException("customer downstream offline"));
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isServiceUnavailable());

        setUp();
        Mockito.when(eventEligibilityPort.isEligible("event-1"))
                .thenThrow(new RestClientException("404 Not Found from event-service"));
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isServiceUnavailable());

        setUp();
        Mockito.when(ticketInventoryEligibilityPort.isEligible("event-1", "VIP", 2))
                .thenThrow(new RestClientException("inventory-service unavailable"));
        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "eventId": "event-1",
                                  "ticketType": "VIP",
                                  "quantity": 2,
                                  "totalPrice": 99.99,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isServiceUnavailable());

        mockMvc.perform(get("/api/ticket-orders/{id}", 999999))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/ticket-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    private long extractLongField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":";
        int start = json.indexOf(pattern);
        int valueStart = start + pattern.length();
        int valueEnd = json.indexOf(",", valueStart);
        if (valueEnd < 0) {
            valueEnd = json.indexOf("}", valueStart);
        }
        return Long.parseLong(json.substring(valueStart, valueEnd).trim());
    }
}
