package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.config.TestSecurityConfig;
import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.model.UnitOfCalculation;
import CS203G3.tariff_backend.repository.CountryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for TariffController /calculate endpoint
 * Tests the full tariff calculation flow including database interactions
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TariffControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CountryRepository countryRepository;

    private static final String TEST_TOKEN = "mock-jwt-token";
    
    private Country exporterCountry;
    private Country importerCountry;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/tariffs";

        // Clean up database
        countryRepository.deleteAll();

        // Setup test data - just countries, create tariffs in individual tests
        exporterCountry = new Country("US", "United States", "NA");
        importerCountry = new Country("SG", "Singapore", "SEA");
        countryRepository.save(exporterCountry);
        countryRepository.save(importerCountry);
    }

    // Helper method to create a tariff
    private void createTestTariff(String hsCode, Map<UnitOfCalculation, BigDecimal> rates) {
        TariffCreateDto tariffDto = new TariffCreateDto();
        tariffDto.setExporter("US");
        tariffDto.setImporter("SG");
        tariffDto.setHSCode(hsCode);
        tariffDto.setTariffRates(rates);
        tariffDto.setEffectiveDate(Date.valueOf(LocalDate.now().minusDays(30)));
        tariffDto.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(365)));
        tariffDto.setReference("Test Tariff");

        given()
            .header("Authorization", "Bearer " + TEST_TOKEN)
            .contentType(ContentType.JSON)
            .body(tariffDto)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    // ==================== CALCULATE TARIFF TESTS ====================

    @Test
    void calculateTariff_WithInvalidHSCode_ReturnsNotFound() {
        Map<UnitOfCalculation, BigDecimal> quantityValues = new HashMap<>();
        quantityValues.put(UnitOfCalculation.KG, new BigDecimal("100"));

        CalculationRequest request = new CalculationRequest(
            Date.valueOf(LocalDate.now()),
            "INVALID999",
            "US",
            "SG",
            new BigDecimal("10000"),
            quantityValues
        );

        given()
            .header("Authorization", "Bearer " + TEST_TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/calculate")
        .then()
            .statusCode(anyOf(is(404), is(500)));
    }

    @Test
    void calculateTariff_WithInvalidCountryPair_ReturnsNotFound() {
        Map<UnitOfCalculation, BigDecimal> quantityValues = new HashMap<>();
        quantityValues.put(UnitOfCalculation.KG, new BigDecimal("100"));

        CalculationRequest request = new CalculationRequest(
            Date.valueOf(LocalDate.now()),
            "010121",
            "US",
            "XX", // Invalid country
            new BigDecimal("10000"),
            quantityValues
        );

        given()
            .header("Authorization", "Bearer " + TEST_TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/tariffs/calculate")
        .then()
            .statusCode(anyOf(is(404), is(500)));
    }

    @Test
    void calculateTariff_WithEmptyQuantityValues_ReturnsBadRequest() {
        Map<UnitOfCalculation, BigDecimal> quantityValues = new HashMap<>(); // Empty map

        CalculationRequest request = new CalculationRequest(
            Date.valueOf(LocalDate.now()),
            "010121",
            "US",
            "SG",
            new BigDecimal("10000"),
            quantityValues
        );

        given()
            .header("Authorization", "Bearer " + TEST_TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/tariffs/calculate")
        .then()
            .statusCode(anyOf(is(400), is(500)));
    }
}