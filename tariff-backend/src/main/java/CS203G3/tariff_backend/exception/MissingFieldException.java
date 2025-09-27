package CS203G3.tariff_backend.exception;

public class MissingFieldException extends BusinessException {
    public MissingFieldException(String message) {
        super("MISSING_FIELD", message);
    }
    
}
