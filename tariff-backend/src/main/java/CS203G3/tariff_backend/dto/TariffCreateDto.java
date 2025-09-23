package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;

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
    private Integer HSCode;

    @NotNull(message = "Rate is required")
    private BigDecimal rate;

    @NotNull(message = "EffectiveDate is required")
    @FutureOrPresent(message = "EffectiveDate must be today or in the future")
    private Date effectiveDate;  // JavaScript Date object

    @Future(message = "ExpiryDate must be in the future")
    private Date expiryDate;     // JavaScript Date object

    @Size(max = 255, message = "Reference cannot exceed 255 characters")
    private String reference;
    
    // Constructors
    public TariffCreateDto() {}
    
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