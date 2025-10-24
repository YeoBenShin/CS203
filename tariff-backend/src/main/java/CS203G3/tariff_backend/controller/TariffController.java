package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.dto.UnitInfoDto;
import CS203G3.tariff_backend.model.UnitOfCalculation;
import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.service.TariffService;

import java.util.List;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * REST Controller for Tariff API endpoints
 * Uses DTOs for better frontend integration
 */
@RestController
@RequestMapping("/api/tariffs")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from your frontend
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Get all tariffs
     * GET /api/tariffs
     */
    @GetMapping
    public ResponseEntity<List<TariffDto>> getAllTariffRates() {
        List<TariffDto> tariffs = tariffService.getAllTariffRates();
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
    public ResponseEntity<List<TariffDto>> getTariffById(@PathVariable Long id) {
        List<TariffDto> tariffs = tariffService.getTariffById(id);
        return ResponseEntity.ok(tariffs);
    }

    /**
     * Create a new tariff
     * POST /api/tariffs
     */
    @PostMapping
    public ResponseEntity<TariffDto> createTariff(@RequestBody TariffCreateDto createDto) {
        System.out.println("Received TariffCreateDto:");
        System.out.println("Exporter: " + createDto.getExporter());
        System.out.println("HSCode: " + createDto.getHSCode());
        System.out.println("EffectiveDate: " + createDto.getEffectiveDate());
        System.out.println("ExpiryDate: " + createDto.getExpiryDate());
        System.out.println("Reference: " + createDto.getReference());
        
        TariffDto createdTariff = tariffService.createTariff(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTariff);
    }

    /**
     * Update an existing tariff
     * PUT /api/tariffs/{tariffRateId}
     */
    @PutMapping("/{tariffId}")
    public ResponseEntity<TariffDto> updateTariff(@PathVariable Long tariffId, @RequestBody TariffCreateDto updateDto) {
        // System.out.println("Received update for Tariff ID: " + tariffId);
        // System.out.println("UpdateDTO: " + updateDto);
        TariffDto updatedTariff = tariffService.updateTariffRate(tariffId, updateDto);
        return ResponseEntity.ok(updatedTariff);
    }

    /**
     * Delete a tariff
     * DELETE /api/tariffs/{id}
     */
    @DeleteMapping("/{tariffId}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long tariffId) {
        tariffService.deleteTariff(tariffId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tariffs by HS Code
     * GET /api/tariffs/hSCode/{hSCode}
     */
    @GetMapping("/hSCode/{hSCode}")
    public ResponseEntity<List<TariffDto>> getTariffsByHSCode(@PathVariable String hSCode) {
        List<TariffDto> tariffs = tariffService.getTariffsByHSCode(hSCode);
        return ResponseEntity.ok(tariffs);
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResult> calculateTariff(@RequestBody CalculationRequest calculationDto) {
        CalculationResult calculationResult = tariffService.calculateTariff(calculationDto);
        return ResponseEntity.ok(calculationResult);
    }
    
    @GetMapping("/unit-info")
    public ResponseEntity<List<UnitOfCalculation>> getTariffUnitInfo(
        @RequestParam String hSCode, 
        @RequestParam String importCountry, 
        @RequestParam String exportCountry, 
        @RequestParam Date tradeDate) {
        List<UnitOfCalculation> unitInfo = tariffService.getUnitInfo(hSCode, importCountry, exportCountry, tradeDate);
        return ResponseEntity.ok(unitInfo);
    }
}
