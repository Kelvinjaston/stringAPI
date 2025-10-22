package stringAnalyzer.stringAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;
import java.util.LinkedHashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomExceptions.NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(CustomExceptions.NotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(CustomExceptions.ConflictException.class)
    public ResponseEntity<Object> handleConflictException(CustomExceptions.ConflictException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(CustomExceptions.InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(CustomExceptions.InvalidInputException ex) {
        return createErrorResponse(ex.getStatus(), ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Invalid parameter type for '%s'. Value '%s' cannot be converted to the required type.",
                ex.getName(),
                ex.getValue()
        );
        return createErrorResponse(HttpStatus.BAD_REQUEST, message);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }
    private ResponseEntity<Object> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
