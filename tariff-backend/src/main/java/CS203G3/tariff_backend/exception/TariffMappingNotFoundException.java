package CS203G3.tariff_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Exception thrown when a TariffMapping is not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TariffMappingNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public TariffMappingNotFoundException(Long id) {
        super("TariffMapping not found with id: " + id);
    }
    
    
}