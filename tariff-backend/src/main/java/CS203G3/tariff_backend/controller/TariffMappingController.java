package CS203G3.tariff_backend.controller;


import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.service.TariffMappingService;
import CS203G3.tariff_backend.exception.TariffMappingNotFoundException;
import CS203G3.tariff_backend.dto.TariffMappingDto;
import CS203G3.tariff_backend.dto.TariffMappingCreateDto;


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
@RequestMapping("/api/tariffmappings") // Better URL structure
public class TariffMappingController {

    @Autowired
    private TariffMappingService tariffMappingService; 

    /**
     * Get all tariff mappings
     * GET /api/tariffmappings
     */
    @GetMapping
    public ResponseEntity<List<TariffMappingDto>> getAllTariffMappings() {
        List<TariffMappingDto> tariffMappingsDtos = tariffMappingService.getAllTariffMappings();
        return ResponseEntity.ok(tariffMappingsDtos);
    }

    /**
     * Get tariff mapping by ID
     * GET /api/tariffmappings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TariffMappingDto> getTariffMappingById(@PathVariable Long id) {
        try {
            TariffMappingDto tariffMappingDto = tariffMappingService.getTariffMappingById(id);
            return ResponseEntity.ok(tariffMappingDto);
        } catch (TariffMappingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new tariff mapping
     * POST /api/tariffmappings
     */
    @PostMapping
    public ResponseEntity<TariffMappingDto> createTariffMapping(@RequestBody TariffMappingCreateDto tariffMappingCreateDto) {
        TariffMappingDto createdTariffMapping = tariffMappingService.createTariffMapping(tariffMappingCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTariffMapping);
    }

    /**
     * Update an existing tariff mapping
     * PUT /api/tariffmappings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TariffMapping> updateTariffMapping(@PathVariable Long id, @RequestBody TariffMappingDto tariffMappingDto) {
        try {
            TariffMapping updatedTariffMapping = tariffMappingService.updateTariffMapping(id, tariffMappingDto);
            return ResponseEntity.ok(updatedTariffMapping);
        } catch (TariffMappingNotFoundException e) {
            return ResponseEntity.notFound().build();
        } 
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffmappings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariffMapping(@PathVariable Long id) {
        try {
            tariffMappingService.deleteTariffMapping(id);
            return ResponseEntity.noContent().build();
        } catch (TariffMappingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

