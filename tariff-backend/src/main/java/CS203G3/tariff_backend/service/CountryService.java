package CS203G3.tariff_backend.service;

import java.util.List;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.exception.CountryNotFoundException;


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
     * @throws CountryNotFoundException if country not found
     */
    Country getCountryById(String id) throws CountryNotFoundException;

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
     * @throws CountryNotFoundException if country not found
     */
    Country updateCountry(String id, Country country) throws CountryNotFoundException;

    /**
     * Delete a country
     * @param id The country ID to delete
     * @throws CountryNotFoundException if country not found
     */
    void deleteCountry(String id) throws CountryNotFoundException;
}
