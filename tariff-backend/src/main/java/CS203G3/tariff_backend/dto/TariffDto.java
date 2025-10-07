package CS203G3.tariff_backend.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * DTO for Tariff responses - includes all tariff data plus mapping details for frontend
 */

public class TariffDto {
    private Long tariffID;

    // Core tariff fields
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    
    // fields for frontend display from exporter country
    private String exporterCode;
    private String exporterName;
    private String importerCode;
    private String importerName;

    // fields for frontend display from product
    @JsonProperty("hSCode")
    private String hSCode;
    private String productDescription;

    List <TariffRateBreakdownDto> tariffRates;
    // Constructors
    public TariffDto() {}

    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
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

    public String getImporterCode() {
        return importerCode;
    }

    public void setImporterCode (String importerCode) {
        this.importerCode = importerCode;
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

    public String gethSCode() {
        return hSCode;
    }

    public void setHSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<TariffRateBreakdownDto> getTariffRates() {
        return tariffRates;
    }

    public void setTariffRates(List<TariffRateBreakdownDto> tariffRates) {
        this.tariffRates = tariffRates;
    }
}
