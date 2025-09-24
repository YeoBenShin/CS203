package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {
    
    // Custom query methods can be added here
    // For example:
    // List<Tariff> findByTariffMappingID(Long tariffMappingID);
    // List<Tariff> findByEffectiveDateBetween(Date start, Date end);

    List<Tariff> findTariffRatesByTariffMappingAndEffectiveDate(TariffMapping tariffMapping, Date effectiveDate);
    
}