package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

public class ImmutableFieldChangeException extends BusinessException {
    public ImmutableFieldChangeException(String fieldName) {
        super("IMMUTABLE_FIELD_CHANGE", 
              String.format("Cannot modify immutable field: %s", fieldName));
    }
    
    public ImmutableFieldChangeException(String message, String fieldName) {
        super("IMMUTABLE_FIELD_CHANGE", 
              String.format("%s: %s", message, fieldName));
    }
}
