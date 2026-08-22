package com.example.marketplace.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage);
    }

    @ExceptionHandler({ AccessDeniedException.class, SecurityException.class })
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(RuntimeException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, String error, String message) {
        ErrorResponseDto response = new ErrorResponseDto(
                status.value(),
                error,
                message,
                LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }
}
