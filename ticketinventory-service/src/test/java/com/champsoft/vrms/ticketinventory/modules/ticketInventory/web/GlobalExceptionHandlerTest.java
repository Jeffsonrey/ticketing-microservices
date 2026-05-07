package com.champsoft.vrms.ticketinventory.modules.ticketInventory.web;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.DuplicateTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.InvalidTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.TicketInventoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlerMapsNotFoundConflictAndBadRequestResponses() throws Exception {
        MockHttpServletRequest request = request("/api/ticket-inventories");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "eventId", "eventId is required"));
        Method method = ValidationSample.class.getDeclaredMethod("sample", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        ResponseEntity<ApiErrorResponse> notFound = handler.handleNotFound(
                new TicketInventoryNotFoundException("not found"),
                request
        );
        ResponseEntity<ApiErrorResponse> conflict = handler.handleConflict(
                new DuplicateTicketInventoryException("duplicate"),
                request
        );
        ResponseEntity<ApiErrorResponse> validation = handler.handleBadRequest(
                new MethodArgumentNotValidException(methodParameter, bindingResult),
                request
        );
        ResponseEntity<ApiErrorResponse> invalid = handler.handleBadRequest(
                new InvalidTicketInventoryException("bad inventory"),
                request
        );
        ResponseEntity<ApiErrorResponse> internalError = handler.handleAny(new RuntimeException(), request);

        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody().message()).isEqualTo("eventId is required");
        assertThat(invalid.getBody().message()).isEqualTo("bad inventory");
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
        void sample(String eventId) {
        }
    }
}
