package CS203G3.tariff_backend.dto;

import java.util.List;

public class UnitInfoDto {
    private String unit; // For backward compatibility
    private List<String> units; // New field for multiple units

    // Constructor for single unit (backward compatibility)
    public UnitInfoDto(String unit) {
        this.unit = unit;
    }

    // Constructor for multiple units
    public UnitInfoDto(List<String> units) {
        this.units = units;
        // Set the single unit field to the first unit for backward compatibility
        this.unit = (units != null && !units.isEmpty()) ? units.get(0) : null;
    }

    // Getters and Setters
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getUnits() {
        return units;
    }

    public void setUnits(List<String> units) {
        this.units = units;
        // Update the single unit field when units are set
        this.unit = (units != null && !units.isEmpty()) ? units.get(0) : null;
    }
}