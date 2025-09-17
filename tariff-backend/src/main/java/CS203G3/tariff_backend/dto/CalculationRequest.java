package CS203G3.tariff_backend.dto;

/**
 * DTO for calculation request
 * Input for tariff cost calculations
 */
public class CalculationRequest {
    private double prodCost;
    private int quantity;
    private double rate;

    // Constructors
    public CalculationRequest() {}

    public CalculationRequest(double prodCost, int quantity, double rate) {
        this.prodCost = prodCost;
        this.quantity = quantity;
        this.rate = rate;
    }

    // Getters and Setters
    public double getProdCost() { 
        return prodCost; 
    }
    
    public void setProdCost(double prodCost) { 
        this.prodCost = prodCost; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }
    
    public double getRate() { 
        return rate; 
    }
    
    public void setRate(double rate) { 
        this.rate = rate; 
    }

    @Override
    public String toString() {
        return "CalculationRequest{" +
                "prodCost=" + prodCost +
                ", quantity=" + quantity +
                ", rate=" + rate +
                '}';
    }
}