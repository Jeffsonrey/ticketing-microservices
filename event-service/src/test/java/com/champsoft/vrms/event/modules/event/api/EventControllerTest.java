package com.champsoft.vrms.event.modules.event.api;

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
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void eventHappyPathFlowWorks() throws Exception {
        String response = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Controller Event",
                                  "description": "Controller flow",
                                  "venueName": "MTELUS",
                                  "venueAddress": "59 Sainte-Catherine St E",
                                  "venueCity": "Montreal",
                                  "startDateTime": "2026-06-01T19:00:00",
                                  "endDateTime": "2026-06-01T22:00:00",
                                  "totalCapacity": 150
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Controller Event"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = extractStringField(response, "id");

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(post("/api/events/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/events/{id}/eligibility", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true));

        mockMvc.perform(put("/api/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Controller Event Updated",
                                  "totalCapacity": 175
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Controller Event Updated"));

        mockMvc.perform(post("/api/events/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(delete("/api/events/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void eventNegativePathsReturnExpectedStatuses() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "",
                                  "venueName": "",
                                  "venueAddress": "",
                                  "venueCity": "",
                                  "startDateTime": null,
                                  "endDateTime": null,
                                  "totalCapacity": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        String duplicateJson = """
                {
                  "title": "Duplicate Event",
                  "description": "Live jazz concert in Montreal",
                  "venueName": "Place des Arts",
                  "venueAddress": "175 Sainte-Catherine St W",
                  "venueCity": "Montreal",
                  "startDateTime": "2026-05-01T19:00:00",
                  "endDateTime": "2026-05-01T22:00:00",
                  "totalCapacity": 500
                }
                """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/events/{id}", "missing-event"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/events/{id}", "missing-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "totalCapacity": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    private String extractStringField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":\"";
        int start = json.indexOf(pattern);
        int valueStart = start + pattern.length();
        int valueEnd = json.indexOf("\"", valueStart);
        return json.substring(valueStart, valueEnd);
    }
}
