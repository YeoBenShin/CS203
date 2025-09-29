package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;

import CS203G3.tariff_backend.model.UnitOfCalculation;

/**
 * DTO for calculation request
 * Input for tariff cost calculations
 */
public class CalculationRequest {
    private int hSCode;
    private String exporter;
    private BigDecimal productValue;
    private UnitOfCalculation productQtyType1;
    private UnitOfCalculation productQtyType2;
    private BigDecimal productQtyValue1;
    private BigDecimal productQtyValue2;
    private Date tradeDate;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(int hSCode, String exporter, BigDecimal productValue, UnitOfCalculation productQtyType1,
            UnitOfCalculation productQtyType2, BigDecimal productQtyValue1, BigDecimal productQtyValue2,
            Date tradeDate) {
        this.hSCode = hSCode;
        this.exporter = exporter;
        this.productValue = productValue;
        this.productQtyType1 = productQtyType1;
        this.productQtyType2 = productQtyType2;
        this.productQtyValue1 = productQtyValue1;
        this.productQtyValue2 = productQtyValue2;
        this.tradeDate = tradeDate;
    }

    public int gethSCode() {
        return hSCode;
    }

    public void sethSCode(int hSCode) {
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

    public UnitOfCalculation getProductQtyType1() {
        return productQtyType1;
    }

    public void setProductQtyType1(UnitOfCalculation productQtyType1) {
        this.productQtyType1 = productQtyType1;
    }

    public UnitOfCalculation getProductQtyType2() {
        return productQtyType2;
    }

    public void setProductQtyType2(UnitOfCalculation productQtyType2) {
        this.productQtyType2 = productQtyType2;
    }

    public BigDecimal getProductQtyValue1() {
        return productQtyValue1;
    }

    public void setProductQtyValue1(BigDecimal productQtyValue1) {
        this.productQtyValue1 = productQtyValue1;
    }

    public BigDecimal getProductQtyValue2() {
        return productQtyValue2;
    }

    public void setProductQtyValue2(BigDecimal productQtyValue2) {
        this.productQtyValue2 = productQtyValue2;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }


}