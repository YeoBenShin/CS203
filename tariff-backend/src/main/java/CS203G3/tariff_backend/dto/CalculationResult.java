package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import CS203G3.tariff_backend.model.UnitOfCalculation;

/**
 * DTO for calculation result Output for tariff cost calculations
 */
public class CalculationResult {

    private BigDecimal netTotal;
    private String tariffName;
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    private List<TariffBreakdown> tariffs;

    // Constructors
    public CalculationResult() {
    }

    public CalculationResult(BigDecimal netTotal, String tariffName, Date effectiveDate, Date expiryDate,
            String reference, List<TariffBreakdown> tariffs) {
        this.netTotal = netTotal;
        this.tariffName = tariffName;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.reference = reference;
        this.tariffs = tariffs;
    }

    // Getters/Setters
    public BigDecimal getNetTotal() { return netTotal; }
    public void setNetTotal(BigDecimal netTotal) { this.netTotal = netTotal; }

    public List<TariffBreakdown> getTariffs() { return tariffs; }
    public void setTariffs(List<TariffBreakdown> tariffs) { this.tariffs = tariffs; }

    public String getTariffName() { return tariffName; }
    public void setTariffName(String tariffName) { this.tariffName = tariffName; }

    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    // Frontend convenience fields
    public BigDecimal getTotalCost() { return netTotal; }

    public BigDecimal getTotalTariffCost() {
      // Sum all duty amounts from breakdowns
      if (tariffs != null) {
        return tariffs.stream()
            .map(TariffBreakdown::getTariffCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      }
      return BigDecimal.ZERO;
    }

    // Sum only AV rates as percentage
    public BigDecimal getTotalTariffRate() {
      if (tariffs != null) {
        return tariffs.stream()
            .filter(t -> t.getType() == UnitOfCalculation.AV)
            .map(TariffBreakdown::getTariffRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      }
      return BigDecimal.ZERO;
    }
}
