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
    // Frontend fields
    private String hsCode;
    private String country;
    private BigDecimal shippingCost;
    private String tradeDate;
    
    // Legacy fields for backward compatibility
    private String hSCode;
    private String exporter;
    private String importer;
    private BigDecimal productValue;
    private Map<UnitOfCalculation, BigDecimal> quantityValues;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(String hsCode, String country, BigDecimal shippingCost, String tradeDate) {
        this.hsCode = hsCode;
        this.country = country;
        this.shippingCost = shippingCost;
        this.tradeDate = tradeDate;
    }

    // Frontend field getters and setters
    public String getHsCode() {
        return hsCode != null ? hsCode : hSCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getCountry() {
        return country != null ? country : exporter;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getShippingCost() {
        return shippingCost != null ? shippingCost : productValue;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
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
        return getCountry();
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
        return getShippingCost();
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

    // Utility method to convert string date to SQL Date
    public Date getTradeDataAsDate() {
        if (tradeDate != null) {
            try {
                return Date.valueOf(tradeDate);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}