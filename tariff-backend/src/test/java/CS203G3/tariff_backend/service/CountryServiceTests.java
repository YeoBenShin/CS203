package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTests {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country country;
    private List<Country> countryList;

    @BeforeEach
    void setUp() {
        // Setup sample country
        country = new Country("US", "United States", "NA");
        
        Country country2 = new Country("SG", "Singapore", "SEA");
        Country country3 = new Country("CN", "China", "AS");
        
        countryList = Arrays.asList(country, country2, country3);
    }

    @Test
    void getAllCountries_ReturnsAllCountries() {
        // Arrange
        when(countryRepository.findAll()).thenReturn(countryList);

        // Act
        List<Country> result = countryService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("US", result.get(0).getIsoCode());
        assertEquals("SG", result.get(1).getIsoCode());
        assertEquals("CN", result.get(2).getIsoCode());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void getAllCountries_WhenEmpty_ReturnsEmptyList() {
        // Arrange
        when(countryRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Country> result = countryService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void getCountryById_WhenExists_ReturnsCountry() {
        // Arrange
        when(countryRepository.findById("US")).thenReturn(Optional.of(country));

        // Act
        Country result = countryService.getCountryById("US");

        // Assert
        assertNotNull(result);
        assertEquals("US", result.getIsoCode());
        assertEquals("United States", result.getName());
        assertEquals("NA", result.getRegion());
        verify(countryRepository, times(1)).findById("US");
    }

    @Test
    void getCountryById_WhenNotExists_ThrowsException() {
        // Arrange
        when(countryRepository.findById("XX")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> countryService.getCountryById("XX"));
        verify(countryRepository, times(1)).findById("XX");
    }

    @Test
    void createCountry_WithValidData_CreatesAndReturnsCountry() {
        // Arrange
        when(countryRepository.save(any(Country.class))).thenReturn(country);

        // Act
        Country result = countryService.createCountry(country);

        // Assert
        assertNotNull(result);
        assertEquals("US", result.getIsoCode());
        assertEquals("United States", result.getName());
        assertEquals("NA", result.getRegion());
        verify(countryRepository, times(1)).save(country);
    }

    @Test
    void updateCountry_WhenExists_UpdatesAndReturnsCountry() {
        // Arrange
        Country updatedData = new Country("US", "United States of America", "North America");
        Country updatedCountry = new Country("US", "United States of America", "North America");
        
        when(countryRepository.findById("US")).thenReturn(Optional.of(country));
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);

        // Act
        Country result = countryService.updateCountry("US", updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("US", result.getIsoCode());
        assertEquals("United States of America", result.getName());
        assertEquals("North America", result.getRegion());
        verify(countryRepository, times(1)).findById("US");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    void updateCountry_WhenNotExists_ThrowsException() {
        // Arrange
        Country updatedData = new Country("XX", "Unknown", "Unknown");
        when(countryRepository.findById("XX")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> countryService.updateCountry("XX", updatedData));
        verify(countryRepository, times(1)).findById("XX");
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void deleteCountry_WhenExists_DeletesCountry() {
        // Arrange
        when(countryRepository.existsById("US")).thenReturn(true);
        doNothing().when(countryRepository).deleteById("US");

        // Act
        countryService.deleteCountry("US");

        // Assert
        verify(countryRepository, times(1)).existsById("US");
        verify(countryRepository, times(1)).deleteById("US");
    }

    @Test
    void deleteCountry_WhenNotExists_ThrowsException() {
        // Arrange
        when(countryRepository.existsById("XX")).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> countryService.deleteCountry("XX"));
        verify(countryRepository, times(1)).existsById("XX");
        verify(countryRepository, never()).deleteById(anyString());
    }
}