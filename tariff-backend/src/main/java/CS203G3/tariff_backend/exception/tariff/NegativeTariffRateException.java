package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when tariff rate is negative
 */
public class NegativeTariffRateException extends BusinessException {
    
    public NegativeTariffRateException(String message) {
        super("NEGATIVE_TARIFF_RATE", message);
    }
}