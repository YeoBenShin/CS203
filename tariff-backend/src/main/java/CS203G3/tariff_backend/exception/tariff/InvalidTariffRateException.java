package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when tariff rate exceeds allowed maximum
 */
public class InvalidTariffRateException extends BusinessException {
    
    public InvalidTariffRateException(String message) {
        super("INVALID_TARIFF_RATE", message);
    }
}