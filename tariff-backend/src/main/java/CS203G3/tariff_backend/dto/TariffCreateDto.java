package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DurationFormat.Unit;

import com.fasterxml.jackson.annotation.JsonProperty;

import CS203G3.tariff_backend.model.UnitOfCalculation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TariffCreateDto {
    
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Exporter is required")
    private String exporter;

    @NotNull(message = "HS Code is required")
    @JsonProperty("hSCode")
    private String hSCode;

    @NotNull(message = "Rate is required")
    private BigDecimal rate;

    @NotNull(message = "EffectiveDate is required")
    @FutureOrPresent(message = "EffectiveDate must be today or in the future")
    private Date effectiveDate;  // JavaScript Date object

    @Future(message = "ExpiryDate must be in the future")
    private Date expiryDate;     // JavaScript Date object

    @Size(max = 255, message = "Reference cannot exceed 255 characters")
    private String reference;

    @NotNull(message = "UnitOfCalculation is required")
    private UnitOfCalculation unitOfCalculation;

    @NotNull(message = "TariffRate is required")
    private BigDecimal tariffRate;

    
    // Constructors
    public TariffCreateDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String gethSCode() {
        return hSCode;
    }

    public void sethSCode(String hSCode) {
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

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }

    public BigDecimal getTariffRate() {
        return rate;
    }

    public void setTariffRate(BigDecimal rate) {
        this.rate = rate;
    }

}