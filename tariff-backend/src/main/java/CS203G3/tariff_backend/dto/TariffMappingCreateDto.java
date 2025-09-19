package CS203G3.tariff_backend.dto;

public class TariffMappingCreateDto {
    private String exporter;
    private String importer;
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
