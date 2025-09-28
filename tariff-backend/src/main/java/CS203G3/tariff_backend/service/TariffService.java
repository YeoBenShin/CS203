package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.model.Tariff;

import java.util.List;
import java.sql.Date;

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
     * Get tariffs by page number
     * @return List of tariffs within specified page(batch) number
     */
    List<TariffDto> getTariffsByPage(int page, int pageSize);
    
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
     * Delete a tariff
     * @param id The tariff ID to delete
     */
    void deleteTariff(Long id);
    
    /**
     * Find tariffs by HS Code
     * @param hsCode The HS Code
     * @return List of tariff DTOs for this HS Code
     */
    List<TariffDto> getTariffsByhSCode(Integer hsCode);
}