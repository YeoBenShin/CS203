package CS203G3.tariff_backend.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCalculationMap;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.CountryPair;
import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffRate;
import CS203G3.tariff_backend.model.UnitOfCalculation;
import CS203G3.tariff_backend.repository.CountryPairRepository;
import CS203G3.tariff_backend.repository.TariffRateRepository;
import CS203G3.tariff_backend.repository.TariffRepository;

@Service
public class CalculationServiceImpl implements CalculationService {

    private final TariffRepository tariffRepository;
    private final TariffRateRepository tariffRateRepository;
    private final TariffCalculationService tariffCalculationService;
    private final CountryPairRepository countryPairRepository;

    @Autowired
    public CalculationServiceImpl(
            TariffRepository tariffRepository,
            TariffRateRepository tariffRateRepository,
            TariffCalculationService tariffCalculationService,
            CountryPairRepository countryPairRepository
    ) {
        this.tariffRepository = tariffRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.tariffCalculationService = tariffCalculationService;
        this.countryPairRepository = countryPairRepository;
    }

    @Override
    public CalculationResult calculateTariff(CalculationRequest request) {
        // Validate required fields
        String hsCode = request.getHsCode();
        String exporterCountry = request.getExporter();
        String importerCountry = request.getImporter();
        Date tradeDate = request.getTradeDataAsDate();

        if (hsCode == null || hsCode.isBlank()) {
            throw new IllegalArgumentException("HS Code is required");
        }
        if (exporterCountry == null || exporterCountry.isBlank()) {
            throw new IllegalArgumentException("Exporting country is required");
        }
        if (importerCountry == null || importerCountry.isBlank()) {
            throw new IllegalArgumentException("Importing country is required");
        }
        if (tradeDate == null) {
            throw new IllegalArgumentException("Trade date is required and must be in YYYY-MM-DD format");
        }
        // Require shipping/product value as AV base (if you want it mandatory)
        if (request.getProductValue() == null) {
            throw new IllegalArgumentException("Shipping cost / product value is required");
        }

        // Find the country pair
        List<CountryPair> countryPair = countryPairRepository.findByExporterAndImporter(exporterCountry, importerCountry);
        if (countryPair == null || countryPair.isEmpty()) {
            throw new ResourceNotFoundException("No country pair found for exporter: " + exporterCountry + " and importer: " + importerCountry);
        }

        // Find valid tariff for the given HS code, country pair, and trade date
        Optional<Tariff> tariffOpt = tariffRepository.findValidTariff(
                request.getHSCode(),
                countryPair,
                request.getTradeDataAsDate()
        );
        Tariff tariff = tariffOpt.orElseThrow(() -> new RuntimeException("No tariff found"));

        // Build calculation maps
        List<TariffCalculationMap> tariffCalculationMaps = new ArrayList<>();

        // Fetch ALL TariffRate records that belong to this tariffId (handles multiple rows with same tariffId)
        Long tariffId = tariff.getTariffID();
        List<TariffRate> tariffRates = tariffRateRepository.findAllByTariff_TariffID(tariffId);
        // Fallback: if repository returns null/empty and tariff has embedded rates, attempt to use them
        if (tariffRates == null || tariffRates.isEmpty()) {
            tariffRates = tariff.getTariffRates() != null ? tariff.getTariffRates() : List.of();
        }

        // Pull quantities entered by the user (e.g., { KG: 12.5, G: 300 })
        var quantitiesByUnit = request.getQuantityValues(); // may be empty

        for (TariffRate tariffRate : tariffRates) {
            UnitOfCalculation unit = tariffRate.getUnitOfCalculation();
            BigDecimal rate = tariffRate.getTariffRate() != null ? tariffRate.getTariffRate() : BigDecimal.ZERO;

            // AV uses product value; non-AV uses entered quantity for that unit
            BigDecimal valueForCalc;
            if (unit == UnitOfCalculation.AV) {
                valueForCalc = request.getProductValue() != null ? request.getProductValue() : BigDecimal.ZERO;
            } else {
                BigDecimal q = (quantitiesByUnit != null) ? quantitiesByUnit.get(unit) : null;
                valueForCalc = (q != null) ? q : BigDecimal.ZERO;
            }

            TariffCalculationMap map = new TariffCalculationMap(unit, rate, valueForCalc);
            tariffCalculationMaps.add(map);
        }

        // Delegate to calculation engine
        CalculationResult result = tariffCalculationService.calculate(tariffCalculationMaps, request.getProductValue());

        // Enrich result with tariff metadata (if needed)
        result.setTariffName(tariff.getTariffName());
        result.setEffectiveDate(tariff.getEffectiveDate());
        result.setExpiryDate(tariff.getExpiryDate());
        result.setReference(tariff.getReference());

        return result;
    }
}
