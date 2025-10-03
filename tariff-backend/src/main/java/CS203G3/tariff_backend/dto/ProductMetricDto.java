package CS203G3.tariff_backend.dto;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class ProductMetricDto {
    private Long id;
    private String hSCode;
    private String description;
    private UnitOfCalculation unitOfCalculation;

    public ProductMetricDto() {}

    public ProductMetricDto(Long id, String hSCode, String description, UnitOfCalculation unitOfCalculation) {
        this.id = id;
        this.hSCode = hSCode;
        this.description = description;
        this.unitOfCalculation = unitOfCalculation;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
