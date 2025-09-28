package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * DTO for calculation request
 * Input for tariff cost calculations
 */
public class CalculationRequest {
    private int hsCode;
    private String country;
    private String tradeDirection;
    private BigDecimal shippingCost;
    private Date tradeDate;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(BigDecimal shippingCost, int hsCode, String country, Date tradeDate) {
        this.shippingCost = shippingCost;
        this.hsCode = hsCode;
        this.country = country;
        this.tradeDate = tradeDate;
    }

    // Getters and Setters
    public BigDecimal getShippingCost() {
        return shippingCost; 
    }
    
    public void setShippingCost(BigDecimal shippingCost) { 
        this.shippingCost = shippingCost; 
    }
    
    public int gethsCode() { 
        return hsCode; 
    }
    
    public void sethsCode(int hsCode) { 
        this.hsCode = hsCode; 
    }
    

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(String tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Override
    public String toString() {
        return "CalculationRequest{" +
                "shippingCost=" + shippingCost +
                ", hsCode=" + hsCode +
                ", country='" + country + '\'' +
                ", tradeDirection='" + tradeDirection + '\'' +
                ", tradeDate=" + tradeDate +
                '}';
    }
}