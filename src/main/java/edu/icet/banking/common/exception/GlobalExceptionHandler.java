package edu.icet.banking.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @ExceptionHandler(BankingException.class)
    public ResponseEntity<ErrorResponse> handleBankingException(BankingException ex, WebRequest request) {
        log.error("Banking exception: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now().format(formatter))
            .status(ex.getStatus().value())
            .path(request.getDescription(false).replace("uri=", ""))
            .error(ex.getStatus().getReasonPhrase())
            .build();

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        String validationMessage = String.join(", ", fieldErrors.values());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode("VALIDATION_ERROR")
            .message(validationMessage)
            .timestamp(LocalDateTime.now().format(formatter))
            .status(400)
            .path(request.getDescription(false).replace("uri=", ""))
            .error("Bad Request")
            .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            WebRequest request) {
        log.error("Resource not found: {}", ex.getRequestURL());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode("NOT_FOUND")
            .message("Resource not found")
            .timestamp(LocalDateTime.now().format(formatter))
            .status(404)
            .path(ex.getRequestURL())
            .error("Not Found")
            .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .timestamp(LocalDateTime.now().format(formatter))
            .status(500)
            .path(request.getDescription(false).replace("uri=", ""))
            .error("Internal Server Error")
            .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

