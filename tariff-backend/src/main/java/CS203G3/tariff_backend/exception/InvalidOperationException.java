package CS203G3.tariff_backend.exception;

/**
 * Exception for invalid business operations
 */
public class InvalidOperationException extends BusinessException {
    
    public InvalidOperationException(String message) {
        super("INVALID_OPERATION", message);
    }
}