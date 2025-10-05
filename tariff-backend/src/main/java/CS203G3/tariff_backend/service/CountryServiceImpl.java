package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.repository.CountryRepository;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CountryServiceImpl implements CountryService {
    
    private final CountryRepository countryRepository;     

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    @Transactional
    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    @Transactional
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
    @Transactional
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
