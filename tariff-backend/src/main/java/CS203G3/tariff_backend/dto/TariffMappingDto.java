package CS203G3.tariff_backend.dto;

public class TariffMappingDto {
    private Long tariffMappingID;
    private String exporter;
    private String importer;
    private Integer HSCode;
    
    public Long getTariffMappingID() {
        return tariffMappingID;
    }
    public void setTariffMappingID(Long tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }
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
