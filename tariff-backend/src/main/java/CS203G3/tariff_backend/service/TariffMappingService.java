package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.dto.TariffMappingDto;
import CS203G3.tariff_backend.dto.TariffMappingCreateDto;
import CS203G3.tariff_backend.exception.TariffMappingNotFoundException;

import java.util.List;

/**
 * Service interface for Tariff business logic
 * Uses exceptions instead of Optional for cleaner code
 */
public interface TariffMappingService {
    
    /**
     * Get all tariff mappings
     * @return List of all tariff mappings
     */
    List<TariffMappingDto> getAllTariffMappings();
    
    /**
     * Get tariff mapping by ID
     * @param id The tariff mapping ID
     * @return The tariff mapping
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    TariffMappingDto getTariffMappingById(Long id) throws TariffMappingNotFoundException;

    /**
     * Create a new tariff mapping with business validation
     * @param tariffMappingCreateDto The tariff mapping to create with fields provided
     * @return The created tariff mapping Dto to update in the future
     */
    TariffMappingDto createTariffMapping(TariffMappingCreateDto tariffMappingCreateDto);

    /**
     * Update an existing tariff mapping
     * @param id The tariff mapping ID to update
     * @param tariffMapping The updated tariff mapping data
     * @return The updated tariff mapping
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    TariffMapping updateTariffMapping(Long id, TariffMappingDto tariffMappingDto) throws TariffMappingNotFoundException;

    /**
     * Delete a tariff mapping
     * @param id The tariff mapping ID to delete
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    void deleteTariffMapping(Long id) throws TariffMappingNotFoundException;

}