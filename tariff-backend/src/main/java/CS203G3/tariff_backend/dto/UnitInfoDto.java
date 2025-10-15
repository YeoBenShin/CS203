package CS203G3.tariff_backend.dto;

public class UnitInfoDto {
    private String unit;

    public UnitInfoDto(String unit) {
        this.unit = unit;
    }

    // Getter and Setter
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}