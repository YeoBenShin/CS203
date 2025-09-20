package CS203G3.tariff_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import CS203G3.tariff_backend.model.TariffMapping;

@Repository
public interface TariffMappingRepository extends JpaRepository<TariffMapping, Long> {
     @Query("SELECT tm FROM TariffMapping tm " +
           "WHERE tm.importer.countryCode = :importerCode " +
           "AND tm.exporter.countryCode = :exporterCode " +
           "AND tm.product.hsCode = :productHsCode")
    List<TariffMapping> findByImporterCountryCodeAndExporterCountryCodeAndProductHsCode(
        String importerCode, 
        String exporterCode, 
        Integer productHsCode
    );
}
