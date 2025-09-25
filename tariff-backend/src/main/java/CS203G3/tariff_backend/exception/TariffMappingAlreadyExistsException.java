package CS203G3.tariff_backend.exception;

public class TariffMappingAlreadyExistsException extends ResourceAlreadyExistsException {

    public TariffMappingAlreadyExistsException(Integer productId, String importer, String exporter) {
        super("TariffMapping", String.format("Product ID: %d, Importer: %s, Exporter: %s", 
                productId, importer, exporter));
    }
}
