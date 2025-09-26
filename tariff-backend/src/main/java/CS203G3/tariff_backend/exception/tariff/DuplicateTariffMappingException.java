package CS203G3.tariff_backend.exception.tariff;

import CS203G3.tariff_backend.exception.BusinessException;

/**
 * Exception thrown when attempting to create duplicate tariff mapping
 */
public class DuplicateTariffMappingException extends BusinessException {
    
    public DuplicateTariffMappingException(String exporter, String importer, Integer hsCode) {
        super("DUPLICATE_TARIFF_MAPPING", 
              String.format("Tariff mapping already exists for %s -> %s, HSCode: %d", 
                            exporter, importer, hsCode));
    }
}