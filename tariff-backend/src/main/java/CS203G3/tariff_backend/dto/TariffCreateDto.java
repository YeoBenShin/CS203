package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import CS203G3.tariff_backend.model.UnitOfCalculation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TariffCreateDto {
    @NotNull(message = "Exporter is required")
    private String exporter;

    @NotNull(message = "Importer is required")
    private String importer;

    @NotNull(message = "HS Code is required")
    private String hSCode;

    @NotNull(message = "EffectiveDate is required")
    @FutureOrPresent(message = "EffectiveDate must be today or in the future")
    private Date effectiveDate;  // JavaScript Date object

    @Future(message = "ExpiryDate must be in the future")
    private Date expiryDate;     // JavaScript Date object

    @Size(max = 255, message = "Reference cannot exceed 255 characters")
    private String reference;

    @NotNull(message = "At least one rate is required")
    Map<UnitOfCalculation, BigDecimal> tariffRates;

    
    // Constructors
    public TariffCreateDto() {}

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
    
    public String getHSCode() {
        return hSCode;
    }

    public void setHSCode(String hSCode) {
        this.hSCode = hSCode;
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

    public Map<UnitOfCalculation, BigDecimal> getTariffRates() {
        return tariffRates;
    }

    public void setTariffRates(Map<UnitOfCalculation, BigDecimal> tariffRates) {
        this.tariffRates = tariffRates;
    }
}