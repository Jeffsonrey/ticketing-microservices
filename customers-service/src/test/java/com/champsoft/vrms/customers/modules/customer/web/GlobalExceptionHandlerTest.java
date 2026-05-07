package com.champsoft.vrms.customers.modules.customer.web;

import com.champsoft.vrms.customers.modules.customer.application.exception.CustomerNotFoundException;
import com.champsoft.vrms.customers.modules.customer.application.exception.DuplicateCustomerEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    void handlerMapsNotFoundConflictBadRequestAndGenericResponses() throws Exception {
        MockHttpServletRequest request = request("/api/customers");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "firstName", "firstName is required"));
        Method method = ValidationSample.class.getDeclaredMethod("sample", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        ResponseEntity<ApiErrorResponse> notFound = handler.handleNotFound(
                new CustomerNotFoundException("missing customer"),
                request
        );
        ResponseEntity<ApiErrorResponse> conflict = handler.handleConflict(
                new DuplicateCustomerEmailException("duplicate email"),
                request
        );
        ResponseEntity<ApiErrorResponse> validation = handler.handleBadRequest(
                new MethodArgumentNotValidException(methodParameter, bindingResult),
                request
        );
        ResponseEntity<ApiErrorResponse> malformed = handler.handleBadRequest(
                new HttpMessageNotReadableException("malformed body", null),
                request
        );
        ResponseEntity<ApiErrorResponse> badRequest = handler.handleBadRequest(
                new IllegalArgumentException("bad input"),
                request
        );
        ResponseEntity<ApiErrorResponse> internalError = handler.handleAny(new RuntimeException(), request);

        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(notFound.getBody().message()).isEqualTo("missing customer");
        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(conflict.getBody().message()).isEqualTo("duplicate email");
        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody().message()).isEqualTo("firstName is required");
        assertThat(malformed.getBody().message()).isEqualTo("malformed body");
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
        void sample(String firstName) {
        }
    }
}
