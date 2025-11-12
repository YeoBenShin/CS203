package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.exception.NoTariffFoundException;
import CS203G3.tariff_backend.exception.ResourceAlreadyExistsException;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.exception.tariff.ImmutableFieldChangeException;
import CS203G3.tariff_backend.exception.tariff.NegativeTariffRateException;
import CS203G3.tariff_backend.exception.tariff.WrongNumberOfArgumentsException;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    private TariffRate tariffRate1;
    private TariffRate tariffRate2;
    private TariffCreateDto updateDto;
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
        tariffRate1 = new TariffRate();
        tariffRate1.setTariffRateID(1L);
        tariffRate1.setTariffRate(new BigDecimal("10.0"));
        tariffRate1.setUnitOfCalculation(UnitOfCalculation.AV);

        tariffRate2 = new TariffRate();
        tariffRate2.setTariffRateID(2L);
        tariffRate2.setTariffRate(new BigDecimal("5.0"));
        tariffRate2.setUnitOfCalculation(UnitOfCalculation.KG);

        // Set up tariff
        tariff = new Tariff();
        tariff.setTariffID(1L);
        tariff.setProduct(product);
        tariff.setCountryPair(countryPair);
        tariff.setEffectiveDate(effectiveDate);
        tariff.setExpiryDate(expiryDate);
        tariff.setReference("TEST-REF-001");
        tariff.setTariffName("Test Tariff");
        tariffRate1.setTariff(tariff);
        tariffRate2.setTariff(tariff);
        tariff.setTariffRates(Arrays.asList(tariffRate1, tariffRate2));

        // Set up update DTO
        updateDto = new TariffCreateDto();
        updateDto.setHSCode(product.getHSCode());
        updateDto.setExporter(exporter.getIsoCode());
        updateDto.setImporter(importer.getIsoCode());
        updateDto.setEffectiveDate(Date.valueOf(LocalDate.now().plusMonths(1)));
        updateDto.setExpiryDate(Date.valueOf(LocalDate.now().plusYears(2)));
        updateDto.setReference("UPDATED-REF-002");
        
        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("12.0"));
        rates.put(UnitOfCalculation.KG, new BigDecimal("6.0"));
        updateDto.setTariffRates(rates);
    }

    @Test
    void getAllTariffRates_ReturnsAllTariffs() {
        // Arrange
        List<TariffRate> tariffRates = Arrays.asList(tariffRate1);
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
        List<TariffRate> tariffRates = Arrays.asList(tariffRate1);
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
        assertEquals(tariffRate1.getTariffRate(), resultDto.getTariffRates().get(0).getRate());
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

    @Test
    void getTariffsByPage_ReturnsCorrectPage() {
        // Arrange
        List<TariffRate> tariffRates = Arrays.asList(tariffRate1, tariffRate2);
        when(tariffRateRepository.findAll()).thenReturn(tariffRates);

        // Act
        List<TariffDto> result = tariffService.getTariffsByPage(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tariffRateRepository, times(1)).findAll();
    }

    @Test
    void getTariffsByPage_WithEmptyRepository_ReturnsEmptyList() {
        // Arrange
        when(tariffRateRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TariffDto> result = tariffService.getTariffsByPage(0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTariffsByHSCode_WithValidHSCode_ReturnsTariffs() {
        // Arrange
        List<TariffRate> tariffRates = Arrays.asList(tariffRate1, tariffRate2);
        when(tariffRateRepository.findAll()).thenReturn(tariffRates);

        // Act
        List<TariffDto> result = tariffService.getTariffsByHSCode(product.getHSCode());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getHSCode(), result.get(0).gethSCode());
    }

    @Test
    void getTariffsByHSCode_WithInvalidHSCode_ReturnsEmptyList() {
        // Arrange
        when(tariffRateRepository.findAll()).thenReturn(Arrays.asList(tariffRate1, tariffRate2));

        // Act
        List<TariffDto> result = tariffService.getTariffsByHSCode("9999");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTariffsByProductAndCountry_WithValidData_ReturnsTariffs() {
        // Arrange
        List<TariffRate> tariffRates = Arrays.asList(tariffRate1, tariffRate2);
        when(productRepository.findById(product.getHSCode())).thenReturn(Optional.of(product));
        when(countryPairRepository.findByImporter_IsoCode(importer.getIsoCode()))
            .thenReturn(Arrays.asList(countryPair));
        when(tariffRateRepository.findAll()).thenReturn(tariffRates);

        // Act
        List<TariffDto> result = tariffService.getTariffsByProductAndCountry(
            product.getHSCode(), importer.getIsoCode());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getHSCode(), result.get(0).gethSCode());
        assertEquals(importer.getIsoCode(), result.get(0).getImporterCode());
    }

    @Test
    void getTariffsByProductAndCountry_WithInvalidProduct_ThrowsException() {
        // Arrange
        when(productRepository.findById("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.getTariffsByProductAndCountry("INVALID", importer.getIsoCode());
        });
    }

    @Test
    void getTariffsByProductAndCountry_WithNoCountryPairs_ReturnsEmptyList() {
        // Arrange
        when(productRepository.findById(product.getHSCode())).thenReturn(Optional.of(product));
        when(countryPairRepository.findByImporter_IsoCode(importer.getIsoCode()))
            .thenReturn(Collections.emptyList());

        // Act
        List<TariffDto> result = tariffService.getTariffsByProductAndCountry(
            product.getHSCode(), importer.getIsoCode());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUnitInfo_WithValidData_ReturnsUnits() {
        // Arrange
        Date tradeDate = Date.valueOf(LocalDate.now());
        when(countryPairRepository.findSingleByExporterAndImporter(
            exporter.getIsoCode(), importer.getIsoCode())).thenReturn(countryPair);
        when(tariffRepository.findByHsCodeAndCountryPairAndTradeDate(
            product.getHSCode(), countryPair, tradeDate)).thenReturn(Optional.of(tariff));
        when(tariffRateRepository.findAllByTariff_TariffID(tariff.getTariffID()))
            .thenReturn(Arrays.asList(tariffRate1, tariffRate2));

        // Act
        List<UnitOfCalculation> result = tariffService.getUnitInfo(
            product.getHSCode(), importer.getIsoCode(), exporter.getIsoCode(), tradeDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(UnitOfCalculation.AV));
        assertTrue(result.contains(UnitOfCalculation.KG));
    }

    @Test
    void getUnitInfo_WithNoCountryPair_ThrowsException() {
        // Arrange
        Date tradeDate = Date.valueOf(LocalDate.now());
        when(countryPairRepository.findSingleByExporterAndImporter(
            exporter.getIsoCode(), importer.getIsoCode())).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.getUnitInfo(product.getHSCode(), importer.getIsoCode(), 
                exporter.getIsoCode(), tradeDate);
        });
    }

    @Test
    void getUnitInfo_WithNoTariff_ThrowsException() {
        // Arrange
        Date tradeDate = Date.valueOf(LocalDate.now());
        when(countryPairRepository.findSingleByExporterAndImporter(
            exporter.getIsoCode(), importer.getIsoCode())).thenReturn(countryPair);
        when(tariffRepository.findByHsCodeAndCountryPairAndTradeDate(
            product.getHSCode(), countryPair, tradeDate)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoTariffFoundException.class, () -> {
            tariffService.getUnitInfo(product.getHSCode(), importer.getIsoCode(), 
                exporter.getIsoCode(), tradeDate);
        });
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

    @Test
    void updateTariffRate_Success_UpdatesDatesAndReferenceAndRates() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffRateRepository.save(any(TariffRate.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TariffDto result = tariffService.updateTariffRate(tariffId, updateDto);

        // Assert
        assertNotNull(result);
        verify(tariffRepository).save(any(Tariff.class));
        verify(tariffRateRepository, times(2)).save(any(TariffRate.class));
        assertEquals(tariffId, result.getTariffID());
    }

    @Test
    void updateTariffRate_TariffNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long tariffId = 999L;
        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });

        verify(tariffRepository, never()).save(any());
    }

    @Test
    void updateTariffRate_AttemptToChangeProduct_ThrowsImmutableFieldChangeException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        updateDto.setHSCode("9999"); // Different product

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        // Act & Assert
        assertThrows(ImmutableFieldChangeException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });

        verify(tariffRepository, never()).save(any());
    }

    @Test
    void updateTariffRate_AttemptToChangeExporter_ThrowsImmutableFieldChangeException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        updateDto.setExporter("JP"); // Different exporter

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        // Act & Assert
        assertThrows(ImmutableFieldChangeException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });

        verify(tariffRepository, never()).save(any());
    }

    @Test
    void updateTariffRate_AttemptToChangeImporter_ThrowsImmutableFieldChangeException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        updateDto.setImporter("CA"); // Different importer

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        // Act & Assert
        assertThrows(ImmutableFieldChangeException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });

        verify(tariffRepository, never()).save(any());
    }

    @Test
    void updateTariffRate_DifferentNumberOfRates_ThrowsWrongNumberOfArgumentsException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("12.0")); // Only 1 rate instead of 2
        updateDto.setTariffRates(rates);

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);

        // Act & Assert
        assertThrows(WrongNumberOfArgumentsException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });
    }

    @Test
    void updateTariffRate_NegativeRate_ThrowsNegativeTariffRateException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("-10.0")); // Negative rate
        rates.put(UnitOfCalculation.KG, new BigDecimal("5.0"));
        updateDto.setTariffRates(rates);

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);

        // Act & Assert
        assertThrows(NegativeTariffRateException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });
    }

    @Test
    void updateTariffRate_MismatchedUnitOfCalculation_ThrowsResourceNotFoundException() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        Map<UnitOfCalculation, BigDecimal> rates = new HashMap<>();
        rates.put(UnitOfCalculation.AV, new BigDecimal("12.0"));
        rates.put(UnitOfCalculation.C, new BigDecimal("5.0")); // C instead of KG
        updateDto.setTariffRates(rates);

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.updateTariffRate(tariffId, updateDto);
        });
    }

    @Test
    void updateTariffRate_UpdateOnlyReference_Success() {
        // Arrange
        Long tariffId = 1L;
        List<TariffRate> existingRates = Arrays.asList(tariffRate1, tariffRate2);

        updateDto.setEffectiveDate(null);
        updateDto.setExpiryDate(null);
        updateDto.setReference("NEW-REF");

        when(tariffRateRepository.findAllByTariff_TariffID(tariffId)).thenReturn(existingRates);
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffRateRepository.save(any(TariffRate.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TariffDto result = tariffService.updateTariffRate(tariffId, updateDto);

        // Assert
        assertNotNull(result);
        verify(tariffRepository).save(argThat(t -> "NEW-REF".equals(t.getReference())));
    }

    @Test
    void deleteTariff_Success_DeletesTariff() {
        // Arrange
        Long tariffId = 1L;
        when(tariffRepository.existsById(tariffId)).thenReturn(true);
        doNothing().when(tariffRepository).deleteById(tariffId);

        // Act
        tariffService.deleteTariff(tariffId);

        // Assert
        verify(tariffRepository).existsById(tariffId);
        verify(tariffRepository).deleteById(tariffId);
    }

    @Test
    void deleteTariff_TariffNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long tariffId = 999L;
        when(tariffRepository.existsById(tariffId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.deleteTariff(tariffId);
        });

        verify(tariffRepository).existsById(tariffId);
        verify(tariffRepository, never()).deleteById(any());
    }

    @Test
    void deleteTariff_WithNullId_ThrowsResourceNotFoundException() {
        // Arrange
        Long tariffId = null;
        when(tariffRepository.existsById(tariffId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            tariffService.deleteTariff(tariffId);
        });

        verify(tariffRepository, never()).deleteById(any());
    }

}
