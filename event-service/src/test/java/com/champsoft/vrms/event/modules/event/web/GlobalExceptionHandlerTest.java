package com.champsoft.vrms.event.modules.event.web;

import com.champsoft.vrms.event.modules.event.application.exception.DuplicateEventException;
import com.champsoft.vrms.event.modules.event.application.exception.EventNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
    void handlerMapsNotFoundAndConflictResponses() {
        MockHttpServletRequest request = request("/api/events/missing");

        ResponseEntity<ApiErrorResponse> notFound = handler.handleNotFound(
                new EventNotFoundException("Event not found"),
                request
        );
        ResponseEntity<ApiErrorResponse> conflict = handler.handleConflict(
                new DuplicateEventException("Duplicate event"),
                request
        );

        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(notFound.getBody().message()).isEqualTo("Event not found");
        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(conflict.getBody().message()).isEqualTo("Duplicate event");
    }

    @Test
    void handlerMapsValidationAndGenericErrors() throws Exception {
        MockHttpServletRequest request = request("/api/events");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "title", "title is required"));
        Method method = ValidationSample.class.getDeclaredMethod("sample", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        ResponseEntity<ApiErrorResponse> validation = handler.handleBadRequest(
                new MethodArgumentNotValidException(methodParameter, bindingResult),
                request
        );
        ResponseEntity<ApiErrorResponse> badRequest = handler.handleBadRequest(
                new IllegalArgumentException("bad input"),
                request
        );
        ResponseEntity<ApiErrorResponse> internalError = handler.handleAny(new RuntimeException(), request);

        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody().message()).isEqualTo("title is required");
        assertThat(badRequest.getStatusCode().value()).isEqualTo(400);
        assertThat(badRequest.getBody().message()).isEqualTo("bad input");
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
        void sample(String title) {
        }
    }
}
