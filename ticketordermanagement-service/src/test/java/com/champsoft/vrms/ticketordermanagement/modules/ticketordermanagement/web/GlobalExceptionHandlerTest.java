package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.web;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.InvalidTicketOrderException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.TicketOrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlerMapsNotFoundBadRequestAndDownstreamResponses() throws Exception {
        MockHttpServletRequest request = request("/api/ticket-orders");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "quantity", "quantity must be at least 1"));
        Method method = ValidationSample.class.getDeclaredMethod("sample", Integer.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        ResponseEntity<ApiErrorResponse> notFound = handler.handleNotFound(
                new TicketOrderNotFoundException("missing order"),
                request
        );
        ResponseEntity<ApiErrorResponse> validation = handler.handleBadRequest(
                new MethodArgumentNotValidException(methodParameter, bindingResult),
                request
        );
        ResponseEntity<ApiErrorResponse> invalid = handler.handleBadRequest(
                new InvalidTicketOrderException("bad order"),
                request
        );
        ResponseEntity<ApiErrorResponse> malformed = handler.handleBadRequest(
                new HttpMessageNotReadableException("malformed body", null),
                request
        );
        ResponseEntity<ApiErrorResponse> downstream = handler.handleDownstream(
                new RestClientException("customer service offline"),
                request
        );
        ResponseEntity<ApiErrorResponse> internalError = handler.handleAny(new RuntimeException(), request);

        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(notFound.getBody().message()).isEqualTo("missing order");
        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody().message()).isEqualTo("quantity must be at least 1");
        assertThat(invalid.getBody().message()).isEqualTo("bad order");
        assertThat(malformed.getBody().message()).isEqualTo("malformed body");
        assertThat(downstream.getStatusCode().value()).isEqualTo(503);
        assertThat(downstream.getBody().message()).isEqualTo("customer service offline");
        assertThat(internalError.getStatusCode().value()).isEqualTo(500);
        assertThat(internalError.getBody().message()).isEqualTo("Unexpected error");
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    static class ValidationSample {
        @SuppressWarnings("unused")
        void sample(Integer quantity) {
        }
    }
}
