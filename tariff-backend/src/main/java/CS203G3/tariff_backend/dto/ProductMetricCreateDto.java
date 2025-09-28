package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class ProductMetricCreateDto {
    private Integer hSCode;
    private UnitOfCalculation unitOfCalculation;

    // Getters and Setters
    public Integer gethSCode() {
        return hSCode;
    }

    public void sethSCode(Integer hSCode) {
        this.hSCode = hSCode;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
}
