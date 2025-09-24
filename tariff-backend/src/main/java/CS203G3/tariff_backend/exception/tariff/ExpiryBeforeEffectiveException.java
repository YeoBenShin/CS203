package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when expiry date is before effective date
 */
public class ExpiryBeforeEffectiveException extends BusinessException {
    
    public ExpiryBeforeEffectiveException(String message) {
        super("EXPIRY_BEFORE_EFFECTIVE", message);
    }
}