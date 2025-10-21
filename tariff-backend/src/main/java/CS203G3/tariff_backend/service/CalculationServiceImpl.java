package CS203G3.tariff_backend.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.dto.CalculationRequest;
import CS203G3.tariff_backend.dto.CalculationResult;
import CS203G3.tariff_backend.dto.TariffCalculationMap;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.CountryPair;
import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffRate;
import CS203G3.tariff_backend.repository.CountryPairRepository;
import CS203G3.tariff_backend.repository.TariffRepository;

@Service
public class CalculationServiceImpl implements CalculationService {

    private final TariffRepository tariffRepository;
    private final CountryPairRepository countryPairRepository;
    private final TariffCalculationService tariffCalculationService;

    public CalculationServiceImpl(TariffRepository tariffRepository,
            CountryPairRepository countryPairRepository,
            TariffCalculationService tariffCalculationService) {
        this.tariffRepository = tariffRepository;
        this.countryPairRepository = countryPairRepository;
        this.tariffCalculationService = tariffCalculationService;
    }

    @Override
    public CalculationResult calculateTariff(CalculationRequest request) {
        // Extract data from request
        String hsCode = request.getHsCode();
        String exporterCountry = request.getExporter();
        String importerCountry = request.getImporter();
        Date tradeDate = request.getTradeDataAsDate();

        if (tradeDate == null) {
            throw new IllegalArgumentException("Invalid trade date format. Expected format: YYYY-MM-DD");
        }

        // Find the country pair
        List<CountryPair> countryPair = countryPairRepository.findByExporterAndImporter(exporterCountry, importerCountry);
        if (countryPair == null) {
            throw new ResourceNotFoundException("No country pair found for exporter: " + exporterCountry + " and importer: " + importerCountry);
        }

        // Find valid tariff for the given HS code, country pair, and trade date
        Optional<Tariff> tariffOpt = tariffRepository.findValidTariff(hsCode, countryPair, tradeDate);
        if (tariffOpt.isEmpty()) {
            throw new ResourceNotFoundException("No valid tariff found for HS Code: " + hsCode
                    + ", exporter: " + exporterCountry + ", importer: " + importerCountry
                    + ", trade date: " + tradeDate);
        }

        Tariff tariff = tariffOpt.get();

        // Create tariff calculation maps from tariff rates
        List<TariffCalculationMap> tariffCalculationMaps = new ArrayList<>();
        for (TariffRate tariffRate : tariff.getTariffRates()) {
            TariffCalculationMap map = new TariffCalculationMap(
                    tariffRate.getUnitOfCalculation(),
                    tariffRate.getTariffRate(),
                    request.getShippingCost() // Use shipping cost as the base value for all calculations
            );
            tariffCalculationMaps.add(map);
        }

        // Calculate the result using existing calculation service
        CalculationResult result = tariffCalculationService.calculate(tariffCalculationMaps, request.getShippingCost());

        // Enrich the result with tariff metadata
        result.setTariffName(tariff.getTariffName());
        result.setEffectiveDate(tariff.getEffectiveDate());
        result.setExpiryDate(tariff.getExpiryDate());
        result.setReference(tariff.getReference());

        return result;
    }
}
