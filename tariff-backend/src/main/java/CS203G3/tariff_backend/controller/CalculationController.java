package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.service.CalculationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for tariff calculation endpoints
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/calculations")
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Calculate total cost with tariff
     * POST /api/calculations/total-cost
     */
    @PostMapping("/total-cost")
    public ResponseEntity<CalculationResult> calculateTotalCost(@RequestBody CalculationRequest request) {
        // Use the rate directly from request (assuming it's already decimal)
        double totalCost = calculationService.calculateTotalCost(
            request.getProdCost(), 
            request.getQuantity(), 
            request.getRate()
        );
        
        // Calculate breakdown for detailed response
        double baseCost = request.getProdCost() * request.getQuantity();
        double tariffAmount = calculationService.calculateTariffAmount(baseCost, request.getRate());
        
        CalculationResult result = new CalculationResult(totalCost, baseCost, tariffAmount);
        return ResponseEntity.ok(result);
    }

    /**
     * Calculate only tariff amount
     * POST /api/calculations/tariff-amount
     */
    @PostMapping("/tariff-amount")
    public ResponseEntity<Double> calculateTariffAmount(@RequestBody CalculationRequest request) {
        double baseCost = request.getProdCost() * request.getQuantity();
        double tariffAmount = calculationService.calculateTariffAmount(baseCost, request.getRate());
        return ResponseEntity.ok(tariffAmount);
    }

    /**
     * Convert percentage to decimal rate
     * GET /api/calculations/percentage-to-decimal?percentage=15.0
     */
    @GetMapping("/percentage-to-decimal")
    public ResponseEntity<Double> percentageToDecimal(@RequestParam double percentage) {
        double decimal = calculationService.percentageToDecimal(percentage);
        return ResponseEntity.ok(decimal);
    }

    /**
     * Convert decimal rate to percentage
     * GET /api/calculations/decimal-to-percentage?decimal=0.15
     */
    @GetMapping("/decimal-to-percentage")
    public ResponseEntity<Double> decimalToPercentage(@RequestParam double decimal) {
        double percentage = calculationService.decimalToPercentage(decimal);
        return ResponseEntity.ok(percentage);
    }
}