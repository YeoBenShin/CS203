package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when tariff periods overlap for the same mapping
 */
public class OverlappingTariffPeriodException extends BusinessException {
    
    public OverlappingTariffPeriodException(String message) {
        super("OVERLAPPING_TARIFF_PERIOD", message);
    }
}