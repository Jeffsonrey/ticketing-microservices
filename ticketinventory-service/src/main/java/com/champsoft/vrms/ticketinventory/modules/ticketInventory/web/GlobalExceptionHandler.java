package com.champsoft.vrms.ticketinventory.modules.ticketInventory.web;

import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.DuplicateTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.InvalidTicketInventoryException;
import com.champsoft.vrms.ticketinventory.modules.ticketInventory.application.exception.TicketInventoryNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(TicketInventoryNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(TicketInventoryNotFoundException ex, HttpServletRequest req) {
		return body(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req);
	}

	@ExceptionHandler(DuplicateTicketInventoryException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateTicketInventoryException ex, HttpServletRequest req) {
		return body(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), req);
	}

	@ExceptionHandler(InvalidTicketInventoryException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalid(InvalidTicketInventoryException ex, HttpServletRequest req) {
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
