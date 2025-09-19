package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.service.TariffService;
import CS203G3.tariff_backend.exception.TariffNotFoundException;
import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Tariff API endpoints
 * Focuses only on HTTP request/response handling
 * Business logic delegated to TariffService
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tariffs") // Better URL structure
public class TariffController {

    @Autowired
    private TariffService tariffService; // Use service, not repository

    /**
     * Get all tariffs
     * GET /api/tariffs
     */
    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        List<Tariff> tariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(tariffs);
    }

    /**
     * Get tariff by ID
     * GET /api/tariffs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTariffById(@PathVariable Long id) {
        try {
            Tariff tariff = tariffService.getTariffById(id);
            return ResponseEntity.ok(tariff);
        } catch (TariffNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tariff not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Create a new tariff
     * POST /api/tariffs
     */
    @PostMapping
    public ResponseEntity<?> createTariff(@RequestBody Tariff tariff) {
        try {
            Tariff createdTariff = tariffService.createTariff(tariff);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTariff);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            String message = e.getMostSpecificCause().getMessage();
            
            if (message.contains("foreign key constraint fails") || message.contains("fk_tariff_mapping")) {
                errorResponse.put("message", "The Tariff Mapping ID you entered does not exist. Please create a mapping first.");
            } else {
                errorResponse.put("message", "Failed to create tariff: " + message);
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update an existing tariff
     * PUT /api/tariffs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTariff(@PathVariable Long id, @RequestBody Tariff tariff) {
        try {
            Tariff updatedTariff = tariffService.updateTariff(id, tariff);
            return ResponseEntity.ok(updatedTariff);
        } catch (TariffNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tariff not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTariff(@PathVariable Long id) {
        try {
            tariffService.deleteTariff(id);
            return ResponseEntity.noContent().build();
        } catch (TariffNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tariff not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get tariffs by mapping ID
     * GET /api/tariffs/mapping/{mappingId}
     */
    @GetMapping("/mapping/{mappingId}")
    public ResponseEntity<?> getTariffsByMappingId(@PathVariable Long mappingId) {
        try {
            List<Tariff> tariffs = tariffService.getTariffsByMappingId(mappingId);
            return ResponseEntity.ok(tariffs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Calculate total cost with tariff
     * POST /api/tariffs/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<CalculationResult> calculateTariff(@RequestBody CalculationRequest request) {
        double totalCost = tariffService.calculateTotalCost(
            request.getProdCost(),
            request.getQuantity(),
            request.getRate()
        );
        
        CalculationResult result = new CalculationResult();
        result.setTotalCost(totalCost);
        return ResponseEntity.ok(result);
    }
}
