package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when effective date is in the past
 */
public class PastEffectiveDateException extends BusinessException {
    
    public PastEffectiveDateException(String message) {
        super("PAST_EFFECTIVE_DATE", message);
    }
}