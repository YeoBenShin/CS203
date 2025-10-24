package CS203G3.tariff_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.exception.MissingFieldException;
import CS203G3.tariff_backend.service.CalculationService;

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

    @PostMapping
    public ResponseEntity<CalculationResult> calculateTariff(@RequestBody CalculationRequest input) {
        if (input.getProductValue() == null || input.getHsCode() == null || input.getImporter() == null || input.getExporter() == null || input.getTradeDate() == null) {
            throw new MissingFieldException("Product Value, HS Code, Importer, Exporter, and Trade Date are required fields.");
        }
        CalculationResult result = calculationService.calculateTariff(input);
        return ResponseEntity.ok(result);
    }
}
