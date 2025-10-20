package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class TariffBreakdown {
    private UnitOfCalculation type;
    private BigDecimal tariffRate;
    private BigDecimal tariffCost;
    private String reference;
    
    public TariffBreakdown(UnitOfCalculation type, BigDecimal tariffRate, BigDecimal tariffCost) {
        this.type = type;
        this.tariffRate = tariffRate;
        this.tariffCost = tariffCost;
    }

    public TariffBreakdown(UnitOfCalculation type, BigDecimal tariffRate, BigDecimal tariffCost, String reference) {
        this.type = type;
        this.tariffRate = tariffRate;
        this.tariffCost = tariffCost;
        this.reference = reference;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}