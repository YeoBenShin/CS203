package CS203G3.tariff_backend.dto;

import jakarta.validation.constraints.NotNull;

public class TariffMappingCreateDto {
    @NotNull(message = "Exporter is required")
    private String exporter;
    @NotNull(message = "Importer is required")
    private String importer;
    @NotNull(message = "HS Code is required")
    private Integer HSCode;
    
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
    public Integer getHSCode() {
        return HSCode;
    }
    public void setHSCode(Integer HSCode) {
        this.HSCode = HSCode;
    }
}
