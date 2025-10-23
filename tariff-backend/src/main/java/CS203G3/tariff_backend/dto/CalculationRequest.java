package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import CS203G3.tariff_backend.model.UnitOfCalculation;

public class CalculationRequest {
    // Frontend fields
    private String hsCode;
    private String country;
    private BigDecimal shippingCost; // AV base if productValue is not provided
    private String tradeDate;

    // Legacy fields
    private String hSCode;
    private String exporter;
    private String importer;
    private BigDecimal productValue;

    // Multi-unit quantities (enum-keyed)
    private Map<UnitOfCalculation, BigDecimal> quantityValues;

    // Getters/setters
    public String getHsCode() { return hsCode != null ? hsCode : hSCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getHSCode() { return getHsCode(); }
    public void setHSCode(String hSCode) { this.hSCode = hSCode; }

    // Derive country if missing so controller validation passes
    public String getCountry() {
        if (country != null) return country;
        if (importer != null) return importer; // treat importer as "country"
        return exporter; // last fallback
    }
    public void setCountry(String country) { this.country = country; }

    public String getExporter() { return exporter; }
    public void setExporter(String exporter) { this.exporter = exporter; }

    public String getImporter() { return importer; }
    public void setImporter(String importer) { this.importer = importer; }

    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }

    // AV base: productValue if set, else shippingCost
    public BigDecimal getProductValue() {
        return productValue != null ? productValue : shippingCost;
    }
    public void setProductValue(BigDecimal productValue) { this.productValue = productValue; }

    public Map<UnitOfCalculation, BigDecimal> getQuantityValues() {
        if (this.quantityValues == null) this.quantityValues = new HashMap<>();
        return this.quantityValues;
    }
    public void setQuantityValues(Map<UnitOfCalculation, BigDecimal> quantityValues) {
        this.quantityValues = quantityValues;
    }

    // Allow JSON: { "quantities": { "KG": 12.5, "G": 300 } }
    public void setQuantities(Map<String, BigDecimal> quantities) {
        if (quantities == null) return;
        if (this.quantityValues == null) this.quantityValues = new HashMap<>();
        quantities.forEach((k, v) -> {
            if (k == null || v == null) return;
            try {
                UnitOfCalculation u = UnitOfCalculation.valueOf(k.trim().toUpperCase());
                this.quantityValues.put(u, v);
            } catch (IllegalArgumentException ignored) {}
        });
    }

    // Utility for DB date lookups
    public Date getTradeDataAsDate() {
        if (tradeDate == null) return null;
        try { return Date.valueOf(tradeDate); } catch (IllegalArgumentException e) { return null; }
    }
}