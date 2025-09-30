package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.dto.TariffCreateDto;
import CS203G3.tariff_backend.dto.TariffUpdateDto;
import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;

import java.util.List;

/**
 * Service interface for Tariff business logic
 */
public interface TariffService {
    
    /**
     * Get all tariffs
     * @return List of all tariff rates grouped by tariff IDs
     */
    List<List<TariffDto>> getAllTariffRates();
    
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
    List<TariffDto> getTariffById(Long id);

    /**
     * Create a new tariff
     * @param createDto The tariff data to create
     * @return The created tariff DTO
     */
    TariffDto createTariff(TariffCreateDto createDto);
    
    /**
     * Update an existing tariff
     * @param id The tariff ID to update
     * @param updateDto The updated tariff data
     * @return The updated tariff DTO
     */
    TariffDto updateTariffRate(Long id, TariffUpdateDto updateDto);
    
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
    List<TariffDto> getTariffsByHSCode(String hsCode);

    
    CalculationResult calculateTariff(CalculationRequest calculationDto);
}