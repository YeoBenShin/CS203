package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class CalculationRequest {
    private Date tradeDate;
    private String hSCode;
    private String exporter;
    private String importer;
    private BigDecimal productValue;
    private Map<UnitOfCalculation, BigDecimal> quantityValues;

    public CalculationRequest(Date tradeDate, String hSCode, String exporter,
            String importer, BigDecimal productValue, Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.tradeDate = tradeDate;
        this.hSCode = hSCode;
        this.exporter = exporter;
        this.importer = importer;
        this.productValue = productValue;
        this.quantityValues = quantityValues;
    }

    // public void setHsCode(String hsCode) {
    //     this.hSCode = hsCode;
    // }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getHsCode() {
        return hSCode;
    }

    public void setHsCode(String hSCode) {
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

    // AV base: productValue if set, else shippingCost
    public BigDecimal getProductValue() {
        return productValue;
    }

    public void setProductValue(BigDecimal productValue) {
        this.productValue = productValue;
    }

    public Map<UnitOfCalculation, BigDecimal> getQuantityValues() {
        if (this.quantityValues == null) this.quantityValues = new HashMap<>();
        return this.quantityValues;
    }
    public void setQuantityValues(Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.quantityValues = quantityValues;
    }

}