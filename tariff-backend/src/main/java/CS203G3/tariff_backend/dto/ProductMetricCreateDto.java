package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class ProductMetricCreateDto {
    private String hSCode;
    private UnitOfCalculation unitOfCalculation;

    // Getters and Setters
    public String getHSCode() {
        return hSCode;
    }

    public void setHSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
}
