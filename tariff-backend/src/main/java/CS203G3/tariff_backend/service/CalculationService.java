package CS203G3.tariff_backend.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for tariff-related calculations
 * Handles business logic for cost computations
 */
@Service
public class CalculationService {

    /**
     * Calculate total cost including tariff
     * @param productCost Base product cost per unit
     * @param quantity Number of units
     * @param tariffRate Tariff rate as decimal (e.g., 0.15 for 15%)
     * @return Total cost including tariff
     */
    public double calculateTotalCost(double productCost, int quantity, double tariffRate) {
        double baseCost = productCost * quantity;
        double tariffAmount = baseCost * tariffRate;
        return baseCost + tariffAmount;
    }

    /**
     * Calculate total cost with BigDecimal for precise calculations
     * @param productCost Base product cost per unit
     * @param quantity Number of units
     * @param tariffRate Tariff rate as BigDecimal
     * @return Total cost including tariff
     */
    public BigDecimal calculateTotalCostPrecise(BigDecimal productCost, int quantity, BigDecimal tariffRate) {
        BigDecimal baseCost = productCost.multiply(BigDecimal.valueOf(quantity));
        BigDecimal tariffAmount = baseCost.multiply(tariffRate);
        return baseCost.add(tariffAmount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate only the tariff amount
     * @param baseCost Total base cost (productCost * quantity)
     * @param tariffRate Tariff rate as decimal
     * @return Tariff amount only
     */
    public double calculateTariffAmount(double baseCost, double tariffRate) {
        return baseCost * tariffRate;
    }

    /**
     * Calculate effective tariff rate as percentage
     * @param tariffAmount Total tariff paid
     * @param baseCost Base cost without tariff
     * @return Effective rate as percentage (e.g., 15.0 for 15%)
     */
    public double calculateEffectiveRate(double tariffAmount, double baseCost) {
        if (baseCost == 0) return 0.0;
        return (tariffAmount / baseCost) * 100.0;
    }

    /**
     * Convert percentage to decimal rate
     * @param percentage Rate as percentage (e.g., 15.0 for 15%)
     * @return Rate as decimal (e.g., 0.15)
     */
    public double percentageToDecimal(double percentage) {
        return percentage / 100.0;
    }

    /**
     * Convert decimal rate to percentage
     * @param decimal Rate as decimal (e.g., 0.15)
     * @return Rate as percentage (e.g., 15.0)
     */
    public double decimalToPercentage(double decimal) {
        return decimal * 100.0;
    }
}