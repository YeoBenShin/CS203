package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.model.UnitOfCalculation;

import java.math.BigDecimal;

public class TariffCalculationMap {
    private UnitOfCalculation unitOfCalculation;
    private BigDecimal rate;
    private BigDecimal value;
    
    public TariffCalculationMap(UnitOfCalculation unitOfCalculation, BigDecimal rate, BigDecimal value) {
        this.unitOfCalculation = unitOfCalculation;
        this.rate = rate;
        this.value = value;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    
}
