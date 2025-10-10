package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import CS203G3.tariff_backend.model.UnitOfCalculation;

/**
 * DTO for calculation request
 * Input for tariff cost calculations
 */
public class CalculationRequest {
    private String hSCode;
    private String exporter;
    private String importer;
    private BigDecimal productValue;
    private Map<UnitOfCalculation, BigDecimal> quantityValues;
    private Instant tradeDate;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(String hSCode, String importer, String exporter, BigDecimal productValue, Map<UnitOfCalculation, BigDecimal> quantityValues,
            Instant tradeDate) {
        this.hSCode = hSCode;
        this.importer = importer;
        this.exporter = exporter;
        this.productValue = productValue;
        this.quantityValues = quantityValues;
        this.tradeDate = tradeDate;
    }

    public String getHSCode() {
        return hSCode;
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

    public Instant getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Instant tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Map<UnitOfCalculation, BigDecimal> getQuantityValues() {
        return quantityValues;
    }

    public void setQuantityValues(Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.quantityValues = quantityValues;
    }


}