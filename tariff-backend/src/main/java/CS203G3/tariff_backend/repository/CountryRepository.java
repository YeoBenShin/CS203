package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    // Additional custom query methods can be added here if needed
}