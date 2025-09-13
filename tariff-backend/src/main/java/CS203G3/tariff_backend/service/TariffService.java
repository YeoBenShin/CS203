package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.exception.TariffNotFoundException;

import java.util.List;

/**
 * Service interface for Tariff business logic
 * Uses exceptions instead of Optional for cleaner code
 */
public interface TariffService {
    
    /**
     * Get all tariffs
     * @return List of all tariffs
     */
    List<Tariff> getAllTariffs();
    
    /**
     * Get tariff by ID
     * @param id The tariff ID
     * @return The tariff
     * @throws TariffNotFoundException if tariff not found
     */
    Tariff getTariffById(Long id) throws TariffNotFoundException;
    
    /**
     * Create a new tariff with business validation
     * @param tariff The tariff to create
     * @return The created tariff
     */
    Tariff createTariff(Tariff tariff);
    
    /**
     * Update an existing tariff
     * @param id The tariff ID to update
     * @param tariff The updated tariff data
     * @return The updated tariff
     * @throws TariffNotFoundException if tariff not found
     */
    Tariff updateTariff(Long id, Tariff tariff) throws TariffNotFoundException;
    
    /**
     * Delete a tariff
     * @param id The tariff ID to delete
     * @throws TariffNotFoundException if tariff not found
     */
    void deleteTariff(Long id) throws TariffNotFoundException;
    
    /**
     * Find tariffs by tariff mapping ID
     * @param tariffMappingId The mapping ID
     * @return List of tariffs for this mapping
     */
    List<Tariff> getTariffsByMappingId(Long tariffMappingId);

    /**
     * Calculate total cost based on tariff rate
     * Business logic for tariff calculations
     * @param productCost Base product cost
     * @param quantity Quantity
     * @param tariffRate Tariff rate (as decimal, e.g., 0.15 for 15%)
     * @return Total cost including tariff
     */
    double calculateTotalCost(double productCost, int quantity, double tariffRate);

   
}