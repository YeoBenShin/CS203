package CS203G3.tariff_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a Tariff is not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TariffNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TariffNotFoundException(Long id) {
        super("Tariff not found with id: " + id);
    }
  
}