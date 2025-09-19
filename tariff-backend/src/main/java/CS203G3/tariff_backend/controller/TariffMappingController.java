package CS203G3.tariff_backend.controller;


import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.service.TariffMappingService;
import CS203G3.tariff_backend.exception.TariffMappingNotFoundException;

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
@RequestMapping("/api/tariffmappings") // Better URL structure
public class TariffMappingController {

    @Autowired
    private TariffMappingService tariffMappingService; 

    /**
     * Get all tariff mappings
     * GET /api/tariffmappings
     */
    @GetMapping
    public ResponseEntity<List<TariffMapping>> getAllTariffMappings() {
        List<TariffMapping> tariffMappings = tariffMappingService.getAllTariffMappings();
        return ResponseEntity.ok(tariffMappings);
    }

    /**
     * Get tariff mapping by ID
     * GET /api/tariffmappings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TariffMapping> getTariffMappingById(@PathVariable Long id) {
        try {
            TariffMapping tariffMapping = tariffMappingService.getTariffMappingById(id);
            return ResponseEntity.ok(tariffMapping);
        } catch (TariffMappingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new tariff mapping
     * POST /api/tariffmappings
     */
    @PostMapping
    public ResponseEntity<?> createTariffMapping(@RequestBody TariffMapping tariffMapping) {
        try {
            System.out.println("Received tariff mapping request: " + tariffMapping);
            
            // Check for null values
            if (tariffMapping.getExporter() == null || tariffMapping.getExporter().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Exporter country code cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            if (tariffMapping.getImporter() == null || tariffMapping.getImporter().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Importer country code cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            if (tariffMapping.getProductId() == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Product ID cannot be null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            TariffMapping createdTariffMapping = tariffMappingService.createTariffMapping(tariffMapping);
            System.out.println("Created tariff mapping: " + createdTariffMapping);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTariffMapping);
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity violation: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create tariff mapping: " + e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update an existing tariff mapping
     * PUT /api/tariffmappings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTariffMapping(@PathVariable Long id, @RequestBody TariffMapping tariffMapping) {
        try {
            TariffMapping updatedTariffMapping = tariffMappingService.updateTariffMapping(id, tariffMapping);
            return ResponseEntity.ok(updatedTariffMapping);
        } catch (TariffMappingNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tariff mapping not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffmappings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTariffMapping(@PathVariable Long id) {
        try {
            tariffMappingService.deleteTariffMapping(id);
            return ResponseEntity.noContent().build();
        } catch (TariffMappingNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Tariff mapping not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

