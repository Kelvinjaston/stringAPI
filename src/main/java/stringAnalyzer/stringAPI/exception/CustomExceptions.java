package stringAnalyzer.stringAPI.exception;
import org.springframework.http.HttpStatus;
public class CustomExceptions {
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }
    public static class InvalidInputException extends RuntimeException {
        private final HttpStatus status;
        public InvalidInputException(String message) {
            super(message);
            this.status = HttpStatus.BAD_REQUEST;
        }
        public InvalidInputException(String message, HttpStatus status) {
            super(message);
            this.status = status;
        }
        public HttpStatus getStatus() {
            return status;
        }
    }
}
