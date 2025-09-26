package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when exporter and importer are the same country
 */
public class SameCountryException extends BusinessException {
    
    public SameCountryException(String country) {
        super("SAME_COUNTRY", 
              String.format("Exporter and importer cannot be the same country: %s", country));
    }
}