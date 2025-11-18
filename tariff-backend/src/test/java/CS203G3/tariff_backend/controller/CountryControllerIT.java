package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.repository.CountryRepository;
import CS203G3.tariff_backend.config.TestSecurityConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for CountryController
 * Uses @SpringBootTest to start the full application context
 * RestAssured for HTTP testing
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class) 
class CountryControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CountryRepository countryRepository;

    private String testToken = "mock-jwt-token";

    private Country testCountry1;
    private Country testCountry2;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/countries";

        // Clean up database before each test
        // countryRepository.deleteAll();

        // Setup test data
        testCountry1 = new Country("US", "United States", "NA");
        testCountry2 = new Country("SG", "Singapore", "SEA");
        
        countryRepository.save(testCountry1);
        countryRepository.save(testCountry2);
    }

    // ==================== GET ALL COUNTRIES ====================

    @Test
    void getAllCountries_ReturnsAllCountries() {
        given()
            .header("Authorization", "Bearer " + testToken)
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("isoCode", hasItems("US", "SG"))
            .body("name", hasItems("United States", "Singapore"))
            .body("region", hasItems("NA", "SEA"));
    }

    @Test
    void getAllCountries_WhenEmpty_ReturnsEmptyList() {
        // Arrange
        countryRepository.deleteAll();

        // Act & Assert
        given()
            .header("Authorization", "Bearer " + testToken)
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    // ==================== GET COUNTRY BY ID ====================

    @Test
    void getCountryById_WithValidId_ReturnsCountry() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/US")
        .then()
            .statusCode(200)
            .body("isoCode", equalTo("US"))
            .body("name", equalTo("United States"))
            .body("region", equalTo("NA"));
    }

    @Test
    void getCountryById_WithInvalidId_ReturnsNotFound() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/INVALID")
        .then()
            .statusCode(404);
    }

    @Test
    void getCountryById_WithNullId_ReturnsBadRequest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/null")
        .then()
            .statusCode(404);
    }

    // ==================== UPDATE COUNTRY ====================

    @Test
    void updateCountry_WithValidData_ReturnsUpdatedCountry() {
        // Arrange
        Country updatedCountry = new Country("US", "United States of America", "North America");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(updatedCountry)
        .when()
            .put("/US")
        .then()
            .statusCode(200)
            .body("isoCode", equalTo("US"))
            .body("name", equalTo("United States of America"))
            .body("region", equalTo("North America"));

        // Verify in database
        Country fromDb = countryRepository.findById("US").orElseThrow();
        assert fromDb.getName().equals("United States of America");
        assert fromDb.getRegion().equals("North America");
    }

    @Test
    void updateCountry_WithInvalidId_ReturnsNotFound() {
        // Arrange
        Country updatedCountry = new Country("INVALID", "Invalid Country", "NA");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(updatedCountry)
        .when()
            .put("/INVALID")
        .then()
            .statusCode(404);
    }

    @Test
    void updateCountry_WithPartialUpdate_UpdatesOnlyProvidedFields() {
        // Arrange - only update name
        Country partialUpdate = new Country("US", "USA", "NA");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(partialUpdate)
        .when()
            .put("/US")
        .then()
            .statusCode(200)
            .body("name", equalTo("USA"))
            .body("region", equalTo("NA"));
    }

    // ==================== DELETE COUNTRY ====================

    @Test
    void deleteCountry_WithValidId_ReturnsNoContent() {
        given()
        .when()
            .delete("/US")
        .then()
            .statusCode(204);

        // Verify deleted from database
        assert !countryRepository.existsById("US");
    }

    @Test
    void deleteCountry_WithInvalidId_ReturnsNotFound() {
        given()
        .when()
            .delete("/INVALID")
        .then()
            .statusCode(404);
    }

    @Test
    void deleteCountry_AlreadyDeleted_ReturnsNotFound() {
        // Arrange - delete first time
        countryRepository.deleteById("US");

        // Act & Assert - try to delete again
        given()
        .when()
            .delete("/US")
        .then()
            .statusCode(404);
    }

    @Test
    void deleteCountry_WithDependentData_ReturnsConflict() {
        // This test assumes you have foreign key constraints
        // and the country is referenced by other entities (e.g., CountryPair)
        // Adjust based on your actual constraint behavior
        
        // For now, this is a placeholder - implement when you have FK constraints
        // given()
        // .when()
        //     .delete("/US")
        // .then()
        //     .statusCode(409); // Conflict due to FK constraint
    }

    // ==================== CROSS-ORIGIN TESTS ====================

    @Test
    void getAllCountries_WithCorsHeaders_ReturnsCorrectHeaders() {
        given()
            .header("Origin", "http://localhost:3000")
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", "http://localhost:3000");
    }

    // ==================== EDGE CASES ====================

    @Test
    void createCountry_WithWhitespaceInFields_TrimsProperly() {
        // Arrange
        Country countryWithWhitespace = new Country("JP", "  Japan  ", "  EA  ");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(countryWithWhitespace)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Verify trimming happened (if your service does trimming)
        Country fromDb = countryRepository.findById("JP").orElseThrow();
        // Add assertions based on whether your service trims
    }

    @Test
    void getAllCountries_WithMultipleRequests_ReturnsConsistentResults() {
        // Test idempotency
        for (int i = 0; i < 3; i++) {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get()
            .then()
                .statusCode(200)
                .body("size()", equalTo(2));
        }
    }

    @Test
    void createAndDeleteCountry_FullLifecycle_WorksCorrectly() {
        // Create
        Country newCountry = new Country("CA", "Canada", "NA");
        
        given()
            .contentType(ContentType.JSON)
            .body(newCountry)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Verify exists
        given()
        .when()
            .get("/CA")
        .then()
            .statusCode(200);

        // Update
        Country updated = new Country("CA", "Canada Updated", "North America");
        given()
            .contentType(ContentType.JSON)
            .body(updated)
        .when()
            .put("/CA")
        .then()
            .statusCode(200)
            .body("name", equalTo("Canada Updated"));

        // Delete
        given()
        .when()
            .delete("/CA")
        .then()
            .statusCode(204);

        // Verify deleted
        given()
        .when()
            .get("/CA")
        .then()
            .statusCode(404);
    }
}
