package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.CountryPair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CountryPairRepository extends JpaRepository<CountryPair, Long> {
    @Query("SELECT cp FROM CountryPair cp WHERE cp.exporter.isoCode = :exporterIso AND cp.importer.isoCode = :importerIso")
    CountryPair findByExporterAndImporter(@Param("exporterIso") String exporterIso, @Param("importerIso") String importerIso);
}
