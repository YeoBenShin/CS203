package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.exception.ResourceAlreadyExistsException;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTests {

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffRateRepository tariffRateRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TariffCalculationService tariffCalculationService;

    @Mock
    private CountryPairRepository countryPairRepository;

    @InjectMocks
    private TariffServiceImpl tariffService;

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
        List<TariffRate> tariffRates = Arrays.asList(tariffRate);
        when(tariffRateRepository.findAll()).thenReturn(tariffRates);

        // Act
        List<TariffDto> result = tariffService.getAllTariffRates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TariffDto resultDto = result.get(0);
        assertEquals(product.getHSCode(), resultDto.gethSCode());
        assertEquals(exporter.getIsoCode(), resultDto.getExporterCode());
        assertEquals(importer.getIsoCode(), resultDto.getImporterCode());
        assertEquals(1, resultDto.getTariffRates().size());
        assertEquals(UnitOfCalculation.AV, resultDto.getTariffRates().get(0).getUnitOfCalculation());
        assertEquals(new BigDecimal("10.0"), resultDto.getTariffRates().get(0).getRate());
        
        // Verify interaction
        verify(tariffRateRepository, times(1)).findAll();
        verifyNoMoreInteractions(tariffRateRepository);
    }

    @Test
    void getTariffById_WhenExists_ReturnsTariff() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> tariffRates = Arrays.asList(tariffRate);
        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(tariffRates);

        // Act
        List<TariffDto> result = tariffService.getTariffById(tariffId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TariffDto resultDto = result.get(0);
        assertEquals(product.getHSCode(), resultDto.gethSCode());
        assertEquals(exporter.getIsoCode(), resultDto.getExporterCode());
        assertEquals(importer.getIsoCode(), resultDto.getImporterCode());
        assertEquals(tariffRate.getTariffRate(), resultDto.getTariffRates().get(0).getRate());
        assertEquals(effectiveDate, resultDto.getEffectiveDate());
        assertEquals(expiryDate, resultDto.getExpiryDate());
        
        // Verify interaction
        verify(tariffRateRepository, times(1)).findAllByTariff_TariffID(tariffId);
        verifyNoMoreInteractions(tariffRateRepository);
    }

    @Test
    void getTariffById_WhenNotExists_ReturnsEmptyList() {
        // Arrange
        Long nonExistentId = 999L;
        when(tariffRateRepository.findAllByTariff_TariffID(nonExistentId))
            .thenReturn(Collections.emptyList());

        // Act
        List<TariffDto> result = tariffService.getTariffById(nonExistentId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        // Verify interaction
        verify(tariffRateRepository, times(1)).findAllByTariff_TariffID(nonExistentId);
        verifyNoMoreInteractions(tariffRateRepository);
    }

    void createTariff_WithValidData_CreatesAndReturnsTariff() {
        // Arrange
        TariffCreateDto createDto = new TariffCreateDto();
        createDto.setExporter(exporter.getIsoCode());
        createDto.setImporter(importer.getIsoCode());
        createDto.setHSCode(product.getHSCode());
        createDto.setEffectiveDate(effectiveDate);
        createDto.setExpiryDate(expiryDate);
        createDto.setReference("TEST-REF-001");
        
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("10.0"));
        createDto.setTariffRates(rates);

        // Stub repository methods
        when(productRepository.findById(product.getHSCode()))
            .thenReturn(Optional.of(product));
        
        when(countryPairRepository.findSingleByExporterAndImporter(
            exporter.getIsoCode(), importer.getIsoCode()))
            .thenReturn(countryPair);
        
        when(tariffRepository.findByProductAndCountryPairAndEffectiveDateAndExpiryDate(
            eq(product), eq(countryPair), eq(effectiveDate), eq(expiryDate)))
            .thenReturn(Optional.empty());
        
        // Stub save to simulate auto-generated ID
        when(tariffRepository.save(any(Tariff.class))).thenAnswer(invocation -> {
            Tariff savedTariff = invocation.getArgument(0);
            savedTariff.setTariffID(1L);
            return savedTariff;
        });
        
        when(tariffRateRepository.save(any(TariffRate.class))).thenAnswer(invocation -> {
            TariffRate savedRate = invocation.getArgument(0);
            savedRate.setTariffRateID(1L);
            return savedRate;
        });

        // Act
        TariffDto result = tariffService.createTariff(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product.getHSCode(), result.gethSCode());
        assertEquals(exporter.getIsoCode(), result.getExporterCode());
        assertEquals(importer.getIsoCode(), result.getImporterCode());
        assertEquals(effectiveDate, result.getEffectiveDate());
        assertEquals(expiryDate, result.getExpiryDate());
        assertEquals("TEST-REF-001", result.getReference());
        
        assertNotNull(result.getTariffRates());
        assertEquals(1, result.getTariffRates().size());
        assertEquals(new BigDecimal("10.0"), result.getTariffRates().get(0).getRate());
        assertEquals(UnitOfCalculation.AV, result.getTariffRates().get(0).getUnitOfCalculation());
        
        // Verify all interactions
        verify(productRepository, times(1)).findById(product.getHSCode());
        verify(countryPairRepository, times(1))
            .findSingleByExporterAndImporter(exporter.getIsoCode(), importer.getIsoCode());
        verify(tariffRepository, times(1))
            .findByProductAndCountryPairAndEffectiveDateAndExpiryDate(
                eq(product), eq(countryPair), eq(effectiveDate), eq(expiryDate));
        verify(tariffRepository, times(1)).save(any(Tariff.class));
        verify(tariffRateRepository, times(1)).save(any(TariffRate.class));
    }

    @Test
    void createTariff_WithDuplicateTariff_ThrowsException() {
        // Arrange
        TariffCreateDto createDto = new TariffCreateDto();
        createDto.setExporter(exporter.getIsoCode());
        createDto.setImporter(importer.getIsoCode());
        createDto.setHSCode(product.getHSCode());
        createDto.setEffectiveDate(effectiveDate);
        createDto.setExpiryDate(expiryDate);
        
        // Add tariff rates to pass validation
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("10.0"));
        createDto.setTariffRates(rates);
        
        when(productRepository.findById(product.getHSCode()))
            .thenReturn(Optional.of(product));
        
        when(countryPairRepository.findSingleByExporterAndImporter(
            exporter.getIsoCode(), importer.getIsoCode()))
            .thenReturn(countryPair);
        
        // Tariff already exists
        when(tariffRepository.findByProductAndCountryPairAndEffectiveDateAndExpiryDate(
            any(), any(), any(), any()))
            .thenReturn(Optional.of(tariff));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, 
            () -> tariffService.createTariff(createDto));
        
        // Verify save was never called
        verify(tariffRepository, never()).save(any(Tariff.class));
        verify(tariffRateRepository, never()).save(any(TariffRate.class));
    }
    
    @Test
    void createTariff_WithInvalidProduct_ThrowsException() {
        // Arrange
        TariffCreateDto createDto = new TariffCreateDto();
        createDto.setExporter(exporter.getIsoCode());
        createDto.setImporter(importer.getIsoCode());
        createDto.setHSCode("INVALID");
        createDto.setEffectiveDate(effectiveDate);
        createDto.setExpiryDate(expiryDate);
        
        // Add tariff rates to pass validation
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("10.0"));
        createDto.setTariffRates(rates);
        
        when(productRepository.findById("INVALID"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> tariffService.createTariff(createDto));
        
        // Verify subsequent repositories were never called
        verify(productRepository, times(1)).findById("INVALID");
        verify(tariffRepository, never()).save(any(Tariff.class));
    }

}
