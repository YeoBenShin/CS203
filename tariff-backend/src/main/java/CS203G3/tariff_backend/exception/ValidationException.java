package CS203G3.tariff_backend.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for validation failures
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends BusinessException {
    private final List<String> errors;
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.errors = List.of(message);
    }

    public ValidationException(String message, List<String> errors) {
        super("VALIDATION_ERROR", message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}