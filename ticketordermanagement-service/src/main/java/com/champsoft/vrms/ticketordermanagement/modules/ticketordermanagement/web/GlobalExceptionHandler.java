package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.web;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.InvalidTicketOrderException;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.exception.TicketOrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(TicketOrderNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(TicketOrderNotFoundException ex, HttpServletRequest req) {
		return body(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req);
	}

	@ExceptionHandler(InvalidTicketOrderException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalid(InvalidTicketOrderException ex, HttpServletRequest req) {
		return body(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, req);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		return body(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Malformed or unreadable request body", req);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
		String message = "Invalid value for parameter '" + ex.getName() + "'";
		return body(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message, req);
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpClientError(HttpClientErrorException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.BAD_REQUEST;
		}
		String error = ex.getStatusText().isEmpty() ? "CLIENT_ERROR" : ex.getStatusText().replace(' ', '_').toUpperCase();
		return body(status, error, ex.getMessage(), req);
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpServerError(HttpServerErrorException ex, HttpServletRequest req) {
		return body(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY", "Upstream service error: " + ex.getMessage(), req);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceAccess(ResourceAccessException ex, HttpServletRequest req) {
		return body(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "Could not reach upstream service: " + ex.getMessage(), req);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
		return body(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"INTERNAL_SERVER_ERROR",
				ex.getMessage() == null ? "Unexpected error" : ex.getMessage(),
				req
		);
	}

	private static ResponseEntity<ApiErrorResponse> body(HttpStatus status, String error, String message, HttpServletRequest req) {
		var res = new ApiErrorResponse(Instant.now(), status.value(), error, message, req.getRequestURI());
		return ResponseEntity.status(status).body(res);
	}
}
