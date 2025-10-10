package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for calculation result
 * Output for tariff cost calculations
 */
public class CalculationResult {
    private BigDecimal netTotal;
    private String tariffName;
    private Instant effectiveDate;  // JavaScript Date object
    private Instant expiryDate;     // JavaScript Date object
    private String reference;
    private List<TariffBreakdown> tariffs;


    // Constructors
    public CalculationResult() {}

    public CalculationResult(BigDecimal netTotal, String tariffName, Instant effectiveDate, Instant expiryDate,
            String reference, List<TariffBreakdown> tariffs) {
        this.netTotal = netTotal;
        this.tariffName = tariffName;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.reference = reference;
        this.tariffs = tariffs;
    }


    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

    public List<TariffBreakdown> getTariffs() {
        return tariffs;
    }

    public void setTariffs(List<TariffBreakdown> tariffs) {
        this.tariffs = tariffs;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public Instant getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}