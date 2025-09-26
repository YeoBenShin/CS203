package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.TariffRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRateRepository extends JpaRepository<TariffRate, Long> {
    
}
