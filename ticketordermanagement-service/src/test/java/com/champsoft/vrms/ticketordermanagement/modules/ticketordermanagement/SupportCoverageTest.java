package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement;

import com.champsoft.vrms.ticketordermanagement.TicketordermanagementServiceApplication;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.Exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class SupportCoverageTest {

    @Test
    void legacyErrorResponseExposesConstructorValues() {
        ErrorResponse response = new ErrorResponse("boom", 400);

        assertThat(response.getMessage()).isEqualTo("boom");
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void applicationMainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.main.web-application-type=none"};

            TicketordermanagementServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(TicketordermanagementServiceApplication.class, args));
        }
    }
}
