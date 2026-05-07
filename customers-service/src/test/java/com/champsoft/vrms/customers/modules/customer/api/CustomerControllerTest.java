package com.champsoft.vrms.customers.modules.customer.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void customerHappyPathAndEligibilityFlowWorks() throws Exception {
        String createJson = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "emailAddress": "jane-controller@example.com",
                  "phoneNumber": "5145551212"
                }
                """;

        String response = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emailAddress").value("jane-controller@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = extractLongField(response, "id");

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/customers/{id}/eligibility", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(put("/api/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Janet",
                                  "lastName": "Doe",
                                  "emailAddress": "jane-controller@example.com",
                                  "phoneNumber": "123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"));

        mockMvc.perform(get("/api/customers/{id}/eligibility", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(delete("/api/customers/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void customerNegativePathsReturnExpectedStatuses() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Doe",
                                  "emailAddress": "bad-email",
                                  "phoneNumber": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Dupe",
                                  "lastName": "One",
                                  "emailAddress": "dupe@example.com",
                                  "phoneNumber": "5145551111"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Dupe",
                                  "lastName": "Two",
                                  "emailAddress": "dupe@example.com",
                                  "phoneNumber": "5145552222"
                                }
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/customers/{id}", 999999))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/customers/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Missing",
                                  "lastName": "Customer",
                                  "emailAddress": "missing@example.com",
                                  "phoneNumber": "5145553333"
                                }
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/customers/{id}/eligibility", 999999))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/customers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Doe",
                                  "emailAddress": "still-bad-email",
                                  "phoneNumber": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/customers")
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
