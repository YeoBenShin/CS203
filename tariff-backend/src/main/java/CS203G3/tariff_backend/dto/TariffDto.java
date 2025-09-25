package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Tariff responses - includes all tariff data plus mapping details for frontend
 */

public class TariffDto {
    private Long tariffID;
    private Long tariffMappingID;
    
    // Core tariff fields
    private BigDecimal rate;
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    
    // fields for frontend display from tariff mapping
    private String exporterCode;
    private String exporterName;
    private String importerCode;
    private String importerName;
    private Integer HSCode;
    private String productDescription;

    // Constructors
    public TariffDto() {}

    // Getters and Setters
    public Long getTariffID() {
        return tariffID;
    }
    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
    }
    public Long getTariffMappingID() {
        return tariffMappingID;
    }
    public void setTariffMappingID(Long tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }
    public BigDecimal getRate() {
        return rate;
    }
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public Date getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    // Additional getters/setters for frontend display
    public String getExporterCode() {
        return exporterCode;
    }
    public void setExporterCode(String exporterCode) {
        this.exporterCode = exporterCode;
    }
    public String getExporterName() {
        return exporterName;
    }
    public void setExporterName(String exporterName) {
        this.exporterName = exporterName;
    }
    public String getImporterName() {
        return importerName;
    }
    public void setImporterName(String importerName) {
        this.importerName = importerName;
    }
    public String getImporterCode() {
        return importerCode;
    }
    public void setImporterCode(String importerCode) {
        this.importerCode = importerCode;
    }
    public Integer getHSCode() {
        return HSCode;
    }
    public void setHSCode(Integer HSCode) {
        this.HSCode = HSCode;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    
}
