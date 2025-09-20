package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.dto.TariffCreateDto;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.sql.Update;

/**
 * Service interface for Tariff business logic
 */
public interface TariffService {
    
    /**
     * Get all tariffs
     * @return List of all tariff DTOs
     */
    List<TariffDto> getAllTariffs();
    
    /**
     * Get tariff by ID
     * @param id The tariff ID
     * @return The tariff DTO
     */
    TariffDto getTariffById(Long id);
    
    /**
     * Create a new tariff
     * @param createDto The tariff data to create
     * @return The created tariff DTO
     */
    TariffDto createTariff(TariffCreateDto createDto);
    
    /**
     * Update an existing tariff
     * @param id The tariff ID to update
     * @param createDto The updated tariff data
     * @return The updated tariff DTO
     */
    TariffDto updateTariff(Long id, TariffCreateDto createDto);

    /**
     * Update tariff rates between two countries for a specific product
     * @param importerCountryCode 
     * @param exporterCountryCode 
     * @param productHsCode 
     * @param newRate 
     * @return 
     */
    List<TariffDto> updateTariffsBetweenCountries(
        String importerCountryCode, 
        String exporterCountryCode, 
        Integer productHsCode, 
        BigDecimal newRate
    );
    
    /**
     * Delete a tariff
     * @param id The tariff ID to delete
     */
    void deleteTariff(Long id);
    
    /**
     * Find tariffs by tariff mapping ID
     * @param tariffMappingId The mapping ID
     * @return List of tariff DTOs for this mapping
     */
    List<TariffDto> getTariffsByMappingId(Long tariffMappingId);
}