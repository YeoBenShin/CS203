package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.service.CountryService;
//import CS203G3.tariff_backend.exception.CountryNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Country API endpoints
 * Focuses only on HTTP request/response handling
 * Business logic delegated to CountryService
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @Autowired
    private CountryService countryService; 

    /**
     * Get all countries
     * GET /api/countries
     */
    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Get country by ID
     * GET /api/countries/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable String id) {

        Country country = countryService.getCountryById(id);
        return ResponseEntity.ok(country);
    }

    /**
     * Create a new country
     * POST /api/countries
     */
    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        Country createdCountry = countryService.createCountry(country);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCountry);
    }

    /**
     * Update an existing country
     * PUT /api/countries/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable String id, @RequestBody Country country) {
        Country updatedCountry = countryService.updateCountry(id, country);
        return ResponseEntity.ok(updatedCountry);
    }

    /**
     * Delete a country
     * DELETE /api/countries/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable String id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}
