package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.dto.TariffForCalDisplayDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for calculation result
 * Output for tariff cost calculations
 */
public class CalculationResult {
    private BigDecimal totalCost;
    private BigDecimal totalTariffRate;
    private BigDecimal totalTariffCost;
    private List<TariffForCalDisplayDto> tariffs;


    // Constructors
    public CalculationResult() {}

    public CalculationResult(BigDecimal totalCost, BigDecimal totalTariffRate, BigDecimal totalTariffCost, List<TariffForCalDisplayDto> tariffs) {
        this.totalCost = totalCost;
        this.totalTariffRate = totalTariffRate;
        this.totalTariffCost = totalTariffCost;
        this.tariffs = tariffs;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalTariffRate() {
        return totalTariffRate;
    }

    public void setTotalTariffRate(BigDecimal totalTariffRate) {
        this.totalTariffRate = totalTariffRate;
    }

    public BigDecimal getTotalTariffCost() {
        return totalTariffCost;
    }

    public void setTotalTariffCost(BigDecimal totalTariffCost) {
        this.totalTariffCost = totalTariffCost;
    }

    public List<TariffForCalDisplayDto> getTariffs() {
        return tariffs;
    }

    public void setTariffs(List<TariffForCalDisplayDto> tariffs) {
        this.tariffs = tariffs;
    }

    @Override
    public String toString() {
        return "CalculationResult [totalCost=" + totalCost + ", totalTariffRate=" + totalTariffRate
                + ", totalTariffCost=" + totalTariffCost + ", tariffs=" + tariffs + "]";
    }
}