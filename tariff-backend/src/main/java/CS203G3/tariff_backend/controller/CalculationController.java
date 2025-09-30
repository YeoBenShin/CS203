package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.service.CalculationServiceImpl;
import CS203G3.tariff_backend.exception.MissingFieldException;
import java.math.BigDecimal;

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
        // Detailed validation with specific error messages
        if (input.getShippingCost() == null) {
            throw new MissingFieldException("Shipping Cost is required");
        }
        if (input.getShippingCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MissingFieldException("Shipping Cost must be greater than 0");
        }
        if (input.gethsCode() < 0) {
            throw new MissingFieldException("Invalid HS Code");
        }
        if (input.getCountry() == null || input.getCountry().trim().isEmpty()) {
            throw new MissingFieldException("Country is required");
        }
        if (input.getTradeDate() == null) {
            throw new MissingFieldException("Trade Date is required");
        }
        
        CalculationResult result = calculationService.calculateTariff(input);
        return ResponseEntity.ok(result);
    }
}