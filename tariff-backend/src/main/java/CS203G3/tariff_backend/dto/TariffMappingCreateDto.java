package CS203G3.tariff_backend.dto;

import jakarta.validation.constraints.NotNull;

public class TariffMappingCreateDto {
    @NotNull(message = "Exporter is required")
    private String exporter;
    @NotNull(message = "Importer is required")
    private String importer;
    @NotNull(message = "ProductId is required")
    private Integer productId;
    
    public String getExporter() {
        return exporter;
    }
    public void setExporter(String exporter) {
        this.exporter = exporter;
    }
    public String getImporter() {
        return importer;
    }
    public void setImporter(String importer) {
        this.importer = importer;
    }
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
