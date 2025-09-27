package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    // Custom query methods can be added here
    // For example:
    // List<Tariff> findByTariffMappingID(Long tariffMappingID);
    // List<Tariff> findByEffectiveDateBetween(Date start, Date end);

    @Query("""
                SELECT t FROM Tariff t
                WHERE t.tariffMapping = :tariffMapping
                  AND :effectiveDate >= t.effectiveDate
                  AND (:effectiveDate <= t.expiryDate OR t.expiryDate IS NULL)
            """)
    List<Tariff> findValidTariffs(TariffMapping tariffMapping, Date effectiveDate);

    /**
     * Find overlapping tariffs for the same tariff mapping within the given date range
     */
    @Query("""
                SELECT t FROM Tariff t
                WHERE t.tariffMapping = :tariffMapping
                  AND (
                    (:newEffectiveDate <= t.effectiveDate AND (:newExpiryDate IS NULL OR :newExpiryDate >= t.effectiveDate))
                    OR
                    (:newEffectiveDate >= t.effectiveDate AND (:newEffectiveDate <= t.expiryDate OR t.expiryDate IS NULL))
                  )
            """)
    List<Tariff> findOverlappingTariffs(
        @Param("tariffMapping") TariffMapping tariffMapping,
        @Param("newEffectiveDate") Date newEffectiveDate,
        @Param("newExpiryDate") Date newExpiryDate
    );

    @Query("""
                SELECT t FROM Tariff t
                WHERE t.tariffMapping = :tariffMapping
                  AND (
                    (:newEffectiveDate <= t.effectiveDate AND (:newExpiryDate IS NULL OR :newExpiryDate >= t.effectiveDate))
                    OR
                    (:newEffectiveDate >= t.effectiveDate AND (:newEffectiveDate <= t.expiryDate OR t.expiryDate IS NULL))
                  )
                  AND t.tariffID <> :currentTariffID
            """)
    List<Tariff> findOverlappingTariffsExcludingCurrent(
        @Param("tariffMapping") TariffMapping tariffMapping,
        @Param("currentTariffID") Long tariffID,
        @Param("newEffectiveDate") Date newEffectiveDate,
        @Param("newExpiryDate") Date newExpiryDate
    );

}