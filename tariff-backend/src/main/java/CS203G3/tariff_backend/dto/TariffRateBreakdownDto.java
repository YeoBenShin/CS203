package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;

import org.springframework.format.annotation.DurationFormat.Unit;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class TariffRateBreakdownDto {
    private Long tariffRateID;
    private UnitOfCalculation unitOfCalculation;
    private BigDecimal rate;

    public Long getTariffRateID() {
        return tariffRateID;
    }

    public void setTariffRateID(Long tariffRateID) {
        this.tariffRateID = tariffRateID;
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
}
