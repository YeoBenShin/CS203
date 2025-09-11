package CS203G3.tariff_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TariffNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TariffNotFoundException(Long id) {
        super("Could not find tariff " + id);
    }

}

