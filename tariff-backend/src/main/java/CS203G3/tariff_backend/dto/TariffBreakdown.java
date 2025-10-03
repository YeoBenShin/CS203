package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.model.UnitOfCalculation;

import java.math.BigDecimal;

public class TariffBreakdown {
    private UnitOfCalculation type;
    private BigDecimal tariffRate;
    private BigDecimal tariffCost;
    
    public TariffBreakdown(UnitOfCalculation type, BigDecimal tariffRate, BigDecimal tariffCost) {
        this.type = type;
        this.tariffRate = tariffRate;
        this.tariffCost = tariffCost;
    }

    public UnitOfCalculation getType() {
        return type;
    }

    public void setType(UnitOfCalculation type) {
        this.type = type;
    }

    public BigDecimal getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }

    public BigDecimal getTariffCost() {
        return tariffCost;
    }

    public void setTariffCost(BigDecimal tariffCost) {
        this.tariffCost = tariffCost;
    }

    
}