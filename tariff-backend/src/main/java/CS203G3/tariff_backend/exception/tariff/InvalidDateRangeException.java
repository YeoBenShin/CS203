package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown for general date-related business logic errors
 */
public class InvalidDateRangeException extends BusinessException {
    
    public InvalidDateRangeException(String message) {
        super("INVALID_DATE_RANGE", message);
    }
}