package CS203G3.tariff_backend.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

import CS203G3.tariff_backend.model.UnitOfCalculation;
import java.math.BigDecimal;

/**
 * DTO for Tariff responses - includes all tariff data plus mapping details for frontend
 */

public class TariffDto {
    private Long tariffID;
    
    private String tariffName;
    
    // Core tariff fields
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    
    // fields for frontend display from exporter country
    private String exporterCode;
    private String exporterName;

    // fields for frontend display from product
    @JsonProperty("hSCode")
    private String hSCode;
    private String productDescription;

    private UnitOfCalculation unitOfCalculation;
    private BigDecimal tariffRate;

    // Constructors
    public TariffDto() {}

    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
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

    public String gethSCode() {
        return hSCode;
    }

    public void sethSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
    
    public BigDecimal getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }
}
