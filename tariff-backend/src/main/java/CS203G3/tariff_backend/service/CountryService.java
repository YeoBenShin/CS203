package CS203G3.tariff_backend.service;

import java.util.List;

import CS203G3.tariff_backend.model.Country;



public interface CountryService {
    /**
     * Get all country mappings
     * @return List of all country mappings
     */
    List<Country> getAllCountries();

    /**
     * Get country by ID
     * @param id The country ID
     * @return The country
     */
    Country getCountryById(String id);

    /**
     * Create a new country with business validation
     * @param country The country to create
     * @return The created country
     */
    Country createCountry(Country country);

    /**
     * Update an existing country
     * @param id The country ID
     * @param country The country data to update
     * @return The updated country
     */
    Country updateCountry(String id, Country country);

    /**
     * Delete a country
     * @param id The country ID to delete
     */
    void deleteCountry(String id);
}
