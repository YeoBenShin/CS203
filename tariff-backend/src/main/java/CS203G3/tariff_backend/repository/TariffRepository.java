package CS203G3.tariff_backend.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import CS203G3.tariff_backend.model.CountryPair;
import CS203G3.tariff_backend.model.Tariff;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    // // Custom query methods can be added here
    // // For example:
    // // List<Tariff> findByTariffMappingID(Long tariffMappingID);
    // // List<Tariff> findByEffectiveDateBetween(Date start, Date end);

    // @Query("""
    //             SELECT t FROM Tariff t
    //             WHERE t.tariffMapping = :tariffMapping
    //               AND :effectiveDate >= t.effectiveDate
    //               AND (:effectiveDate <= t.expiryDate OR t.expiryDate IS NULL)
    //         """)
    // List<Tariff> findValidTariffs(TariffMapping tariffMapping, Date effectiveDate);

    // /**
    //  * Find overlapping tariffs for the same tariff mapping within the given date range
    //  */
    // @Query("""
    //             SELECT t FROM Tariff t
    //             WHERE t.tariffMapping = :tariffMapping
    //               AND (
    //                 (:newEffectiveDate <= t.effectiveDate AND (:newExpiryDate IS NULL OR :newExpiryDate >= t.effectiveDate))
    //                 OR
    //                 (:newEffectiveDate >= t.effectiveDate AND (:newEffectiveDate <= t.expiryDate OR t.expiryDate IS NULL))
    //               )
    //         """)
    // List<Tariff> findOverlappingTariffs(
    //     @Param("tariffMapping") TariffMapping tariffMapping,
    //     @Param("newEffectiveDate") Date newEffectiveDate,
    //     @Param("newExpiryDate") Date newExpiryDate
    // );
   @Query("SELECT t FROM Tariff t WHERE t.product.hSCode = :hSCode AND t.countryPair IN :countryPair AND t.effectiveDate <= :tradeDate AND (t.expiryDate IS NULL OR t.expiryDate >= :tradeDate)")
    Optional<Tariff> findValidTariff(
            @Param("hSCode") String hSCode,
            @Param("countryPair") List<CountryPair> countryPair,
            @Param("tradeDate") Date tradeDate
    );
    
    @Query("SELECT t FROM Tariff t WHERE t.product.hsCode = :hsCode AND t.countryPair.importer.isoCode = :importer AND t.countryPair.exporter.isoCode = :exporter")
    List<Tariff> findByHsCodeAndCountryPair(
        @Param("hsCode") String hsCode,
        @Param("importer") String importer,
        @Param("exporter") String exporter
    );

    @Query("SELECT t FROM Tariff t WHERE t.product = :product AND t.countryPair = :countryPair AND ((:expiryDate IS NULL AND t.expiryDate IS NULL) OR t.expiryDate = :expiryDate) AND t.effectiveDate = :effectiveDate")
    Optional<Tariff> findByProductAndCountryPairAndEffectiveDateAndExpiryDate(
        @Param("product") CS203G3.tariff_backend.model.Product product,
        @Param("countryPair") CountryPair countryPair,
        @Param("effectiveDate") Date effectiveDate,
        @Param("expiryDate") Date expiryDate
    );
}