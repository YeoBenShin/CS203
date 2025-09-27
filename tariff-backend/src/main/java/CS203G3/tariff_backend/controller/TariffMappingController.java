package CS203G3.tariff_backend.controller;


import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.service.TariffMappingService;
import CS203G3.tariff_backend.dto.TariffMappingDto;
import CS203G3.tariff_backend.dto.TariffMappingCreateDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Tariff Mapping API endpoints
 * Focuses only on HTTP request/response handling
 * Business logic delegated to TariffMappingService
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tariff-mappings") // Better URL structure
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
        TariffMappingDto tariffMappingDto = tariffMappingService.getTariffMappingById(id);
        return ResponseEntity.ok(tariffMappingDto);
    }

    /**
     * Create a new tariff mapping
     * POST /api/tariffmappings
     */
    @PostMapping
    public ResponseEntity<TariffMappingDto> createTariffMapping(@RequestBody TariffMappingCreateDto tariffMappingCreateDto) {
        // System.out.println("Creating TariffMapping with DTO: " + tariffMappingCreateDto.getProductId());
        TariffMappingDto createdTariffMapping = tariffMappingService.createTariffMapping(tariffMappingCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTariffMapping);
    }

    /**
     * Update an existing tariff mapping
     * PUT /api/tariffmappings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TariffMappingDto> updateTariffMapping(@PathVariable Long id, @RequestBody TariffMappingDto tariffMappingDto) {
        TariffMappingDto updatedTariffMapping = tariffMappingService.updateTariffMapping(id, tariffMappingDto);
        return ResponseEntity.ok(updatedTariffMapping);
    }
    



    /**
     * Delete a tariff
     * DELETE /api/tariffmappings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariffMapping(@PathVariable Long id) {
        tariffMappingService.deleteTariffMapping(id);
        return ResponseEntity.noContent().build();
    }
}

