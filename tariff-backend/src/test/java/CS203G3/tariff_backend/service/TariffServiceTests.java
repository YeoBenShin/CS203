package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TariffServiceTests {

    @MockBean
    private TariffRepository tariffRepository;

    @MockBean
    private TariffRateRepository tariffRateRepository;

    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private TariffCalculationService tariffCalculationService;

    @MockBean
    private CountryPairRepository countryPairRepository;

    @Autowired
    private TariffService tariffService;

    private Country exporter;
    private Country importer;
    private CountryPair countryPair;
    private Product product;
    private Tariff tariff;
    private TariffRate tariffRate;
    private Date effectiveDate;
    private Date expiryDate;

    @BeforeEach
    void setUp() {
        tariffService = new TariffServiceImpl(
            tariffRepository,
            tariffRateRepository,
            countryRepository,
            productRepository,
            tariffCalculationService,
            countryPairRepository
        );

        // Set up countries and country pair
        exporter = new Country("US", "United States", "NA");
        importer = new Country("SG", "Singapore", "SEA");
        countryPair = new CountryPair(exporter, importer);

        // Set up product
        product = new Product();
        product.setHSCode("1234");
        product.setDescription("Test Product");

        // Set up dates
        effectiveDate = Date.valueOf(LocalDate.now());
        expiryDate = Date.valueOf(LocalDate.now().plusYears(1));

        // Set up tariff rate
        tariffRate = new TariffRate();
        tariffRate.setTariffRate(new BigDecimal("10.0"));
        tariffRate.setUnitOfCalculation(UnitOfCalculation.AV);

        // Set up tariff
        tariff = new Tariff();
        tariff.setTariffID(1L);
        tariff.setProduct(product);
        tariff.setCountryPair(countryPair);
        tariff.setEffectiveDate(effectiveDate);
        tariff.setExpiryDate(expiryDate);
        tariff.setReference("TEST-REF-001");
        tariff.setTariffName("Test Tariff");
        tariffRate.setTariff(tariff);
        tariff.setTariffRates(Arrays.asList(tariffRate));
    }

    @Test
    void getAllTariffRates_ReturnsAllTariffs() {
        // Arrange
        when(tariffRateRepository.findAll()).thenReturn(Arrays.asList(tariffRate));

        // Act
        List<TariffDto> result = tariffService.getAllTariffRates();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        TariffDto resultDto = result.get(0);
        assertNotNull(resultDto.getTariffRates());
        assertFalse(resultDto.getTariffRates().isEmpty());
        assertEquals(UnitOfCalculation.AV, resultDto.getTariffRates().get(0).getUnitOfCalculation());
        assertEquals(product.getHSCode(), resultDto.gethSCode());
        assertEquals(exporter.getIsoCode(), resultDto.getExporterCode());
        assertEquals(importer.getIsoCode(), resultDto.getImporterCode());
    }

    @Test
    void getTariffById_WhenExists_ReturnsTariff() {
        // Arrange
        when(tariffRateRepository.findAllByTariff_TariffID(1L)).thenReturn(Arrays.asList(tariffRate));

        // Act
        List<TariffDto> result = tariffService.getTariffById(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        TariffDto resultDto = result.get(0);
        assertEquals(product.getHSCode(), resultDto.gethSCode());
        assertEquals(exporter.getIsoCode(), resultDto.getExporterCode());
        assertEquals(importer.getIsoCode(), resultDto.getImporterCode());
        assertEquals(tariffRate.getTariffRate(), resultDto.getTariffRates().get(0).getRate());
    }

    @Test
    void getTariffById_WhenNotExists_ReturnsEmptyList() {
        // Arrange
        when(tariffRateRepository.findAllByTariff_TariffID(999L)).thenReturn(List.of());

        // Act
        List<TariffDto> result = tariffService.getTariffById(999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createTariff_WithValidData_CreatesAndReturnsTariff() {
        // Arrange
        when(productRepository.findById(product.getHSCode())).thenReturn(Optional.of(product));
        when(countryRepository.findById(exporter.getIsoCode())).thenReturn(Optional.of(exporter));
        when(countryRepository.findById(importer.getIsoCode())).thenReturn(Optional.of(importer));
        when(countryPairRepository.findSingleByExporterAndImporter(exporter.getIsoCode(), importer.getIsoCode())).thenReturn(countryPair);
        when(tariffRepository.save(any(Tariff.class))).thenAnswer(invocation -> {
            Tariff savedTariff = invocation.getArgument(0);
            savedTariff.setTariffID(1L); // Simulate DB auto-generated ID
            return savedTariff;
        });
        when(tariffRateRepository.save(any(TariffRate.class))).thenAnswer(invocation -> {
            TariffRate savedRate = invocation.getArgument(0);
            savedRate.setTariffRateID(1L); // Simulate DB auto-generated ID
            TariffRate newRate = new TariffRate();
            newRate.setTariffRateID(savedRate.getTariffRateID());
            newRate.setTariffRate(savedRate.getTariffRate());
            newRate.setUnitOfCalculation(savedRate.getUnitOfCalculation());
            newRate.setTariff(savedRate.getTariff());
            return newRate;
        });

        TariffCreateDto createDto = new TariffCreateDto();
        createDto.setExporter(exporter.getIsoCode());
        createDto.setImporter(importer.getIsoCode());
        createDto.setHSCode(product.getHSCode());
        createDto.setEffectiveDate(effectiveDate);
        createDto.setExpiryDate(expiryDate);
        createDto.setReference("TEST-REF-001");
        
        // Add a tariff rate using Map<UnitOfCalculation, BigDecimal>
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("10.0"));
        createDto.setTariffRates(rates);

        // Act
        TariffDto result = tariffService.createTariff(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTariffRates());
        assertFalse(result.getTariffRates().isEmpty());
        assertEquals(product.getHSCode(), result.gethSCode());
        assertEquals(exporter.getIsoCode(), result.getExporterCode());
        assertEquals(importer.getIsoCode(), result.getImporterCode());
        assertEquals(effectiveDate, result.getEffectiveDate());
        assertEquals(expiryDate, result.getExpiryDate());
        assertEquals("TEST-REF-001", result.getReference());
        assertEquals(1, result.getTariffRates().size());
        assertEquals(new BigDecimal("10.0"), result.getTariffRates().get(0).getRate());
        assertEquals(UnitOfCalculation.AV, result.getTariffRates().get(0).getUnitOfCalculation());
    }
}
