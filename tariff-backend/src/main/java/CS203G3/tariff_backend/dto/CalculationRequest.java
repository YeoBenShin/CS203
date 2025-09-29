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
    private String hSCode;
    private String exporter;
    private BigDecimal productValue;
    private Map<UnitOfCalculation, BigDecimal> quantityValues;
    private Date tradeDate;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(String hSCode, String exporter, BigDecimal productValue, Map<UnitOfCalculation, BigDecimal> quantityValues,
            Date tradeDate) {
        this.hSCode = hSCode;
        this.exporter = exporter;
        this.productValue = productValue;
        this.quantityValues = quantityValues;
        this.tradeDate = tradeDate;
    }

    public String gethSCode() {
        return hSCode;
    }

    public void sethSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public BigDecimal getProductValue() {
        return productValue;
    }

    public void setProductValue(BigDecimal productValue) {
        this.productValue = productValue;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Map<UnitOfCalculation, BigDecimal> getQuantityValues() {
        return quantityValues;
    }

    public void setQuantityValues(Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.quantityValues = quantityValues;
    }


}