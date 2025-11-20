package com.company.university.web;

import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.student.application.StudentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ValidationError(
                        f.getField(),
                        f.getRejectedValue(),
                        messageSource.getMessage(f, LocaleContextHolder.getLocale())
                ))
                .toList();

        log.warn("Validation failed at path {}: {}", request.getRequestURI(), validationErrors);

        return buildResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage("validation.failed", null, LocaleContextHolder.getLocale()),
                request.getRequestURI(), validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ValidationError(
                        v.getPropertyPath().toString(),
                        v.getInvalidValue(),
                        messageSource.getMessage(v.getMessage(), null, v.getMessage(), LocaleContextHolder.getLocale())
                ))
                .toList();

        log.warn("Constraint violation at path {}: {}", request.getRequestURI(), validationErrors);

        return buildResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage("constraint.violation", null, LocaleContextHolder.getLocale()),
                request.getRequestURI(), validationErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request at path {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler({StudentNotFoundException.class, LectureNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.info("Resource not found at path {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        log.error("Data integrity violation at path {}: {}", request.getRequestURI(), message, ex);
        return buildResponse(HttpStatus.CONFLICT, messageSource.getMessage("data.integrity.violation", null, message, LocaleContextHolder.getLocale()),
                request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception at path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("unexpected.error", null, "Unexpected error occurred", LocaleContextHolder.getLocale()),
                request.getRequestURI(), null);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, String path, List<ValidationError> errors) {
        ApiError error = new ApiError(status, message, path, errors);
        return new ResponseEntity<>(error, status);
    }

    @Getter
    public static class ApiError {
        private final LocalDateTime timestamp = LocalDateTime.now();
        private final int status;
        private final String error;
        private final String message;
        private final String path;
        private final List<ValidationError> errors;

        public ApiError(HttpStatus status, String message, String path, List<ValidationError> errors) {
            this.status = status.value();
            this.error = status.getReasonPhrase();
            this.message = message;
            this.path = path;
            this.errors = errors;
        }
    }

    public record ValidationError(String field, Object rejectedValue, String message) {
    }
}