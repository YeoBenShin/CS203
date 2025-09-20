package CS203G3.tariff_backend.exception;

/**
 * Exception for validation failures
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}