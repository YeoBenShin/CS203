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

import java.util.List;

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
    public ResponseEntity<Tariff> getTariffById(@PathVariable Long id) {
        try {
            Tariff tariff = tariffService.getTariffById(id);
            return ResponseEntity.ok(tariff);
        } catch (TariffNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new tariff
     * POST /api/tariffs
     */
    @PostMapping
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        Tariff createdTariff = tariffService.createTariff(tariff);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTariff);
    }

    /**
     * Update an existing tariff
     * PUT /api/tariffs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tariff> updateTariff(@PathVariable Long id, @RequestBody Tariff tariff) {
        try {
            Tariff updatedTariff = tariffService.updateTariff(id, tariff);
            return ResponseEntity.ok(updatedTariff);
        } catch (TariffNotFoundException e) {
            return ResponseEntity.notFound().build();
        } 
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long id) {
        try {
            tariffService.deleteTariff(id);
            return ResponseEntity.noContent().build();
        } catch (TariffNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get tariffs by mapping ID
     * GET /api/tariffs/mapping/{mappingId}
     */
    @GetMapping("/mapping/{mappingId}")
    public ResponseEntity<List<Tariff>> getTariffsByMappingId(@PathVariable Long mappingId) {
        List<Tariff> tariffs = tariffService.getTariffsByMappingId(mappingId);
        return ResponseEntity.ok(tariffs);
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
