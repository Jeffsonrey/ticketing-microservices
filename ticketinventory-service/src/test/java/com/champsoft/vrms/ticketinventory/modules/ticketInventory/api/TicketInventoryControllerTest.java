package com.champsoft.vrms.ticketinventory.modules.ticketInventory.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class TicketInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void inventoryHappyPathAndEligibilityEndpointsWork() throws Exception {
        String response = mockMvc.perform(post("/api/ticket-inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": "event-controller-1",
                                  "ticketType": "VIP",
                                  "totalTickets": 100,
                                  "availableTickets": 40
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value("event-controller-1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = extractLongField(response, "id");

        mockMvc.perform(get("/api/ticket-inventories/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/ticket-inventories/{id}/eligibility", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/ticket-inventories/eligibility")
                        .param("eventId", "event-controller-1")
                        .param("ticketType", "VIP")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(put("/api/ticket-inventories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": "event-controller-1",
                                  "ticketType": "VIP",
                                  "totalTickets": 100,
                                  "availableTickets": 25
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableTickets").value(25));

        mockMvc.perform(delete("/api/ticket-inventories/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ticket-inventories/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void inventoryNegativePathsReturnExpectedStatuses() throws Exception {
        mockMvc.perform(post("/api/ticket-inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": "bad-event",
                                  "ticketType": "VIP",
                                  "totalTickets": 10,
                                  "availableTickets": 20
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/ticket-inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": "dupe-event",
                                  "ticketType": "VIP",
                                  "totalTickets": 20,
                                  "availableTickets": 10
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/ticket-inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": "dupe-event",
                                  "ticketType": "VIP",
                                  "totalTickets": 20,
                                  "availableTickets": 10
                                }
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/ticket-inventories/{id}", 999999))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/ticket-inventories")
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
