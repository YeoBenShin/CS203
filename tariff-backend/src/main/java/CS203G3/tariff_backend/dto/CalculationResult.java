package CS203G3.tariff_backend.dto;

/**
 * DTO for calculation result
 * Output for tariff cost calculations
 */
public class CalculationResult {
    private double totalCost;
    private double baseCost;
    private double tariffAmount;

    // Constructors
    public CalculationResult() {}

    public CalculationResult(double totalCost) {
        this.totalCost = totalCost;
    }

    public CalculationResult(double totalCost, double baseCost, double tariffAmount) {
        this.totalCost = totalCost;
        this.baseCost = baseCost;
        this.tariffAmount = tariffAmount;
    }

    // Getters and Setters
    public double getTotalCost() { 
        return totalCost; 
    }
    
    public void setTotalCost(double totalCost) { 
        this.totalCost = totalCost; 
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    public double getTariffAmount() {
        return tariffAmount;
    }

    public void setTariffAmount(double tariffAmount) {
        this.tariffAmount = tariffAmount;
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
                "totalCost=" + totalCost +
                ", baseCost=" + baseCost +
                ", tariffAmount=" + tariffAmount +
                '}';
    }
}