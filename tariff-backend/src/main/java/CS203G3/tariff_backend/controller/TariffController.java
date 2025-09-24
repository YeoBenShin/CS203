package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.service.TariffService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * REST Controller for Tariff API endpoints
 * Uses DTOs for better frontend integration
 */
@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @Autowired
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Get all tariffs
     * GET /api/tariffs
     */
    @GetMapping
    public ResponseEntity<List<TariffDto>> getAllTariffs() {
        List<TariffDto> tariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<TariffDto>> getTariffByPage(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<TariffDto> tariffs = tariffService.getTariffsByPage(page, pageSize);
        return ResponseEntity.ok(tariffs);
    }
    

    /**
     * Get tariff by ID
     * GET /api/tariffs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TariffDto> getTariffById(@PathVariable Long id) {
        TariffDto tariff = tariffService.getTariffById(id);
        return ResponseEntity.ok(tariff);
    }

    /**
     * Create a new tariff
     * POST /api/tariffs
     */
    @PostMapping
    public ResponseEntity<TariffDto> createTariff(@RequestBody TariffCreateDto createDto) {
        System.out.println("Received TariffCreateDto:");
        System.out.println("Exporter: " + createDto.getExporter());
        System.out.println("Importer: " + createDto.getImporter()); 
        System.out.println("HSCode: " + createDto.getHSCode());
        System.out.println("Rate: " + createDto.getRate());
        System.out.println("EffectiveDate: " + createDto.getEffectiveDate());
        System.out.println("ExpiryDate: " + createDto.getExpiryDate());
        System.out.println("Reference: " + createDto.getReference());
        
        TariffDto createdTariff = tariffService.createTariff(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTariff);
    }

    /**
     * Update an existing tariff
     * PUT /api/tariffs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TariffDto> updateTariff(@PathVariable Long id, @RequestBody TariffCreateDto createDto) {
        TariffDto updatedTariff = tariffService.updateTariff(id, createDto);
        return ResponseEntity.ok(updatedTariff);
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long id) {
        tariffService.deleteTariff(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tariffs by mapping ID
     * GET /api/tariffs/mapping/{mappingId}
     */
    @GetMapping("/mapping/{mappingId}")
    public ResponseEntity<List<TariffDto>> getTariffsByMappingId(@PathVariable Long mappingId) {
        List<TariffDto> tariffs = tariffService.getTariffsByMappingId(mappingId);
        return ResponseEntity.ok(tariffs);
    }
}
