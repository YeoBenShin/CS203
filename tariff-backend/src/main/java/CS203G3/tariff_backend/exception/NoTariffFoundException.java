package CS203G3.tariff_backend.exception;

public class NoTariffFoundException extends BusinessException {
    public NoTariffFoundException(String message) {
        super("NO_TARIFF_FOUND", message);
    }
    
}
