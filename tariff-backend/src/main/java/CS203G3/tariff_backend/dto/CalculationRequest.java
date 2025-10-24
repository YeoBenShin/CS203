package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

import CS203G3.tariff_backend.model.UnitOfCalculation;

/**
 * DTO for calculation request
 * Input for tariff cost calculations
 */
public class CalculationRequest {
    private Date tradeDate;
    private String hSCode;
    private String exporter;
    private String importer;
    private BigDecimal productValue;
    private Map<UnitOfCalculation, BigDecimal> quantityValues;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(Date tradeDate, String hSCode, String exporter,
            String importer, BigDecimal productValue, Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.tradeDate = tradeDate;
        this.hSCode = hSCode;
        this.exporter = exporter;
        this.importer = importer;
        this.productValue = productValue;
        this.quantityValues = quantityValues;
    }

    public String getHsCode() {
        return hSCode;
    }

    public void setHsCode(String hsCode) {
        this.hSCode = hsCode;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    // Legacy field getters and setters for backward compatibility
    public String getHSCode() {
        return getHsCode();
    }

    public void setHSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public BigDecimal getProductValue() {
        return productValue;
    }

    public void setProductValue(BigDecimal productValue) {
        this.productValue = productValue;
    }

    public Map<UnitOfCalculation, BigDecimal> getQuantityValues() {
        return quantityValues;
    }

    public void setQuantityValues(Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.quantityValues = quantityValues;
    }

}