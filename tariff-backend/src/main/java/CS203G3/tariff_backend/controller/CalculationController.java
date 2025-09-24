package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.service.CalculationServiceImpl;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for tariff calculation endpoints
 */
@RestController
@RequestMapping("/api/calculations")
public class CalculationController {

    private final CalculationServiceImpl calculationService;

    public CalculationController(CalculationServiceImpl calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping
    public ResponseEntity<CalculationResult> calculateTariff(@RequestBody CalculationRequest input) {
        if (input.getShippingCost() == null || input.gethsCode() == 0 || input.getCountry() == null || input.getTradeDirection() == null || input.getTradeDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        CalculationResult result = calculationService.calculateTariff(input);
        return ResponseEntity.ok(result);
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