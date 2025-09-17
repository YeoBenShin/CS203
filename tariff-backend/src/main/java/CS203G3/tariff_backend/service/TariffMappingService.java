package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.TariffMapping;
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
    List<TariffMapping> getAllTariffMappings();
    
    /**
     * Get tariff mapping by ID
     * @param id The tariff mapping ID
     * @return The tariff mapping
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    TariffMapping getTariffMappingById(Long id) throws TariffMappingNotFoundException;

    /**
     * Create a new tariff mapping with business validation
     * @param tariffMapping The tariff mapping to create
     * @return The created tariff mapping
     */
    TariffMapping createTariffMapping(TariffMapping tariffMapping);

    /**
     * Update an existing tariff mapping
     * @param id The tariff mapping ID to update
     * @param tariffMapping The updated tariff mapping data
     * @return The updated tariff mapping
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    TariffMapping updateTariffMapping(Long id, TariffMapping tariffMapping) throws TariffMappingNotFoundException;

    /**
     * Delete a tariff mapping
     * @param id The tariff mapping ID to delete
     * @throws TariffMappingNotFoundException if tariff mapping not found
     */
    void deleteTariffMapping(Long id) throws TariffMappingNotFoundException;

}