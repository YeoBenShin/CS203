package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class TariffForCalDisplayDto {
    private Long tariffID;
    
    // Core tariff fields
    private BigDecimal rate;
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    private BigDecimal amountApplied;

    // Constructors
    public TariffForCalDisplayDto() {}

    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
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

    public BigDecimal getAmountApplied() {
        return amountApplied.divide(BigDecimal.valueOf(100));
    }

    public void setAmountApplied(BigDecimal amountApplied) {
        this.amountApplied = amountApplied;
    }
}