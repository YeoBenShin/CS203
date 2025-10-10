package CS203G3.tariff_backend;

import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.model.UnitOfCalculation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser; // <-- New Import
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

// New Import for CSRF
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
// Apply @WithMockUser to the class with the necessary role (e.g., ADMIN)
// to ensure all secured endpoints are accessible during the test run.
@WithMockUser(username = "tester", roles = {"ADMIN"})
class TariffBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TariffCreateDto testTariffCreateDto;

    @BeforeEach
    void setUp() {
        // Create test data for product code 0101, exporter SGP, importer USA
        testTariffCreateDto = new TariffCreateDto();
        testTariffCreateDto.setExporter("SGP");
        testTariffCreateDto.setImporter("USA");
        testTariffCreateDto.setHSCode("0101");

        // Set effective date to now, truncated to milliseconds for precision consistency
        testTariffCreateDto.setEffectiveDate(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        
        // Set expiry date to one year from now, truncated to milliseconds
        testTariffCreateDto.setExpiryDate(Instant.now().plus(365, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
        
        testTariffCreateDto.setReference("Test tariff for product 0101 SGP to USA");

        // Set up tariff rates
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.KG, new BigDecimal("5.50"));
        rates.put(UnitOfCalculation.PCS, new BigDecimal("0.75"));
        testTariffCreateDto.setTariffRates(rates);
    }

    // Helper method to create a test tariff and return its ID
    private Long createTestTariff() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(testTariffCreateDto);

        MvcResult result = mockMvc.perform(post("/api/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf())) // <-- Added .with(csrf())
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        TariffDto createdTariff = objectMapper.readValue(responseContent, TariffDto.class);
        return createdTariff.getTariffID();
    }

    // ==================== CREATE TESTS ====================

    @Test
    @Order(1)
    void createTariff_ValidData_ShouldReturnCreated() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(testTariffCreateDto);

        mockMvc.perform(post("/api/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exporterCode", is("SGP")))
                .andExpect(jsonPath("$.importerCode", is("USA")))
                .andExpect(jsonPath("$.hSCode", is("0101")))
                .andExpect(jsonPath("$.reference", is("Test tariff for product 0101 SGP to USA")))
                .andExpect(jsonPath("$.tariffID", notNullValue()));
    }

    @Test
    @Order(2)
    void createTariff_InvalidData_ShouldReturnBadRequest() throws Exception {
        // Test with missing required fields
        TariffCreateDto invalidDto = new TariffCreateDto();
        // Missing exporter, importer, hSCode, etc.

        String jsonContent = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/api/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isBadRequest());
    }

    // ==================== READ TESTS ====================
    // GET requests don't need .with(csrf())

    @Test
    @Order(3)
    void getTariffById_ExistingId_ShouldReturnTariff() throws Exception {
        // First create a tariff
        Long tariffId = createTestTariff(); // createTestTariff now includes .with(csrf)

        // Test READ operation
        // Assuming /api/tariffs/{id} returns a list or an object wrapped in a list.
        // The original assertion was `.andExpect(jsonPath("$", isA(List.class)))`
        mockMvc.perform(get("/api/tariffs/{id}", tariffId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tariffID", is(tariffId.intValue()))); // Verify correct ID
    }

    @Test
    @Order(4)
    void getAllTariffs_ShouldReturnList() throws Exception {
        // Create a test tariff first
        createTestTariff();

        // Test READ all operation
        mockMvc.perform(get("/api/tariffs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @Order(5)
    void getTariffsByHSCode_ShouldReturnFilteredList() throws Exception {
        // Create a test tariff first
        createTestTariff();

        // Test READ by HS Code
        mockMvc.perform(get("/api/tariffs/hSCode/{hSCode}", "0101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @Order(6)
    void getTariffsByPage_ShouldReturnPagedResults() throws Exception {
        // Create a test tariff first
        createTestTariff();

        // Test paginated READ operation
        mockMvc.perform(get("/api/tariffs/batch")
                .param("page", "1")
                .param("size", "10") // Added size param for clarity
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @Order(7)
    void updateTariff_ValidData_ShouldReturnUpdated() throws Exception {
        // First create a tariff
        Long tariffId = createTestTariff();

        // Prepare update data
        TariffCreateDto updateDto = new TariffCreateDto();
        updateDto.setExporter("SGP");
        updateDto.setImporter("USA");
        updateDto.setHSCode("0101");
        
        // Set dates with millisecond precision to avoid JSON serialization issues
        updateDto.setEffectiveDate(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        updateDto.setExpiryDate(Instant.now().plus(365, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
        
        updateDto.setReference("Updated test tariff for product 0101");

        // Updated tariff rates
        Map<UnitOfCalculation, BigDecimal> updatedRates = new HashMap<>();
        updatedRates.put(UnitOfCalculation.KG, new BigDecimal("7.25"));
        updatedRates.put(UnitOfCalculation.PCS, new BigDecimal("1.00"));
        updateDto.setTariffRates(updatedRates);

        String jsonContent = objectMapper.writeValueAsString(updateDto);

        // Test UPDATE operation
        mockMvc.perform(put("/api/tariffs/{tariffId}", tariffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exporterCode", is("SGP")))
                .andExpect(jsonPath("$.importerCode", is("USA")))
                .andExpect(jsonPath("$.hSCode", is("0101")))
                .andExpect(jsonPath("$.reference", is("Updated test tariff for product 0101")));
    }

    @Test
    @Order(8)
    void updateTariff_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Prepare update data
        String jsonContent = objectMapper.writeValueAsString(testTariffCreateDto);

        // Test UPDATE operation with non-existing ID
        mockMvc.perform(put("/api/tariffs/{tariffId}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @Order(9)
    void deleteTariff_ExistingId_ShouldReturnNoContent() throws Exception {
        // First create a tariff
        Long tariffId = createTestTariff();

        // Test DELETE operation
        mockMvc.perform(delete("/api/tariffs/{id}", tariffId)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isNoContent());

        // Verify the tariff is deleted by trying to get it
        mockMvc.perform(get("/api/tariffs/{id}", tariffId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    void deleteTariff_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Test DELETE operation with non-existing ID
        mockMvc.perform(delete("/api/tariffs/{id}", 99999L)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isNotFound());
    }

    // ==================== INTEGRATION TEST ====================

    @Test
    @Order(11)
    void crudIntegrationTest_CompleteWorkflow() throws Exception {
        // Integration test covering complete CRUD workflow for product 0101, SGP to USA
        
        // 1. CREATE
        String createJson = objectMapper.writeValueAsString(testTariffCreateDto);
        MvcResult createResult = mockMvc.perform(post("/api/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isCreated())
                .andReturn();

        TariffDto createdTariff = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), TariffDto.class);
        Long tariffId = createdTariff.getTariffID();

        // 2. READ - Get by ID
        mockMvc.perform(get("/api/tariffs/{id}", tariffId))
                .andExpect(status().isOk());

        // 3. UPDATE
        testTariffCreateDto.setReference("Integration test - updated reference");
        String updateJson = objectMapper.writeValueAsString(testTariffCreateDto);
        mockMvc.perform(put("/api/tariffs/{tariffId}", tariffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is("Integration test - updated reference")));

        // 4. DELETE
        mockMvc.perform(delete("/api/tariffs/{id}", tariffId)
                .with(csrf())) // <-- Added .with(csrf())
                .andExpect(status().isNoContent());

        // 5. READ - Verify deletion
        mockMvc.perform(get("/api/tariffs/{id}", tariffId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void contextLoads() {
        // Basic Spring context load test
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
    }

}