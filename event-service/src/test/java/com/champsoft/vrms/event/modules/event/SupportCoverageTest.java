package com.champsoft.vrms.event.modules.event;

import com.champsoft.vrms.event.EventServiceApplication;
import com.champsoft.vrms.event.modules.event.Exception.ErrorResponse;
import com.champsoft.vrms.event.modules.event.api.EventExceptionHandler;
import com.champsoft.vrms.event.modules.event.config.WebConfig;
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
    void simpleSupportTypesCanBeConstructed() {
        assertThat(new EventExceptionHandler()).isNotNull();
        assertThat(new WebConfig()).isNotNull();
    }

    @Test
    void applicationMainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.main.web-application-type=none"};

            EventServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(EventServiceApplication.class, args));
        }
    }
}
