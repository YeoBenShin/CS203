package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.repository.CountryRepository;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private final CountryRepository countryRepository;     

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country createCountry(Country country) {
    
        return countryRepository.save(country);
    }

    @Override
    public void deleteCountry(String id) {
        if (!countryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Country", id);
        }
        countryRepository.deleteById(id);
    }

    @Override
    public List<Country> getAllCountries() {
    
        return countryRepository.findAll();
    }

    @Override
    public Country getCountryById(String id) {
        return countryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Country", id));
    }

    @Override
    public Country updateCountry(String id, Country country) {
        return countryRepository.findById(id)
            .map(existingCountry -> {
                existingCountry.setName(country.getName());
                existingCountry.setRegion(country.getRegion());
                return countryRepository.save(existingCountry);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Country", id));
    }
}
