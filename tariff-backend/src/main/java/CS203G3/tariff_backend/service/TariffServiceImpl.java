package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import CS203G3.tariff_backend.dto.*;
import CS203G3.tariff_backend.exception.*;
import CS203G3.tariff_backend.exception.tariff.*;

import java.sql.Date;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of TariffService with DTO support
 */
@Service
public class TariffServiceImpl implements TariffService {
    private final TariffRateRepository tariffRateRepository;
    private final TariffRepository tariffRepository;
    private final CountryRepository countryRepository;
    private final ProductRepository productRepository;  
    private final CountryPairRepository countryPairRepository;
    private final TariffCalculationService tariffCalculationService; 

    public TariffServiceImpl(TariffRepository tariffRepository, TariffRateRepository tariffRateRepository, 
    CountryRepository countryRepository, ProductRepository productRepository, TariffCalculationService tariffCalculationService, 
 CountryPairRepository countryPairRepository) {
        this.tariffRepository = tariffRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
        this.tariffCalculationService = tariffCalculationService;
        this.countryPairRepository = countryPairRepository;
    }

    private TariffDto convertToDto(List<TariffRate> tariffRates) {
        if (tariffRates == null || tariffRates.isEmpty()) {
            throw new IllegalArgumentException("TariffRate list cannot be null or empty");
        }
        
        TariffDto dto = new TariffDto();
        Tariff tariff = tariffRates.get(0).getTariff();

        dto.setTariffID(tariff.getTariffID());
        // product details
        dto.setHSCode(tariff.getProduct().getHSCode());
        dto.setProductDescription(tariff.getProduct().getDescription());
        // exporter details
        dto.setExporterCode(tariff.getCountryPair().getExporter().getIsoCode());
        dto.setExporterName(tariff.getCountryPair().getExporter().getName());
        // importer details
        dto.setImporterCode(tariff.getCountryPair().getImporter().getIsoCode());
        dto.setImporterName(tariff.getCountryPair().getImporter().getName());

        // tariff details
        dto.setEffectiveDate(tariff.getEffectiveDate());
        dto.setExpiryDate(tariff.getExpiryDate());
        dto.setReference(tariff.getReference());  

        // put the map of tariffrateid : tariffbreakdowndto
        List<TariffRateBreakdownDto> rates = tariffRates.stream()
            .map(this::convertToBreakdownDto)
            .collect(Collectors.toList());

        dto.setTariffRates(rates);
        return dto;
    }

    private TariffRateBreakdownDto convertToBreakdownDto(TariffRate tariffRate) {
        if (tariffRate == null) {
            throw new IllegalArgumentException("TariffRate cannot be null");
        }
        TariffRateBreakdownDto dto = new TariffRateBreakdownDto();
        dto.setTariffRateID(tariffRate.getTariffRateID());
        dto.setUnitOfCalculation(tariffRate.getUnitOfCalculation());
        dto.setRate(tariffRate.getTariffRate());
        return dto;
    }


    private List<TariffRate> convertToEntity(TariffCreateDto createDto) {

        Tariff newTariff = new Tariff();
        Product product = productRepository.findById(createDto.getHSCode())
            .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.getHSCode()));

        String exporterCode = createDto.getExporter();
        String importerCode = createDto.getImporter();

        CountryPair countryPair = countryPairRepository.findSingleByExporterAndImporter(exporterCode, importerCode);

        if (countryPair == null) {
            countryPair = new CountryPair();
            countryPair.setExporter(countryRepository.findById(exporterCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country", exporterCode)));
            countryPair.setImporter(countryRepository.findById(importerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country", importerCode)));
            countryPairRepository.save(countryPair);
        }


        String reference = createDto.getReference();

        // Convert java.util.Date to java.sql.Date
        Date effectiveDate = createDto.getEffectiveDate() != null ? 
            new Date(createDto.getEffectiveDate().getTime()) : null;
        Date expiryDate = createDto.getExpiryDate() != null ? 
            new Date(createDto.getExpiryDate().getTime()) : null;

        // Check for existing tariff with same product, country pair, effective date, and expiry date
        Optional<Tariff> searchTariffOpt = tariffRepository.findByProductAndCountryPairAndEffectiveDateAndExpiryDate(
            product, countryPair, effectiveDate, expiryDate);

        if (searchTariffOpt.isPresent()) {
            throw new ResourceAlreadyExistsException("A tariff with the same product, country pair, effective date, and expiry date already exists.");
        }

        // Check for overlapping tariffs with different dates but same product and country pair
        // This would require a different repository method to find overlapping periods
        // For now, we'll skip this check since the exact same tariff check above covers duplicate prevention

        newTariff.setProduct(product);
        newTariff.setCountryPair(countryPair);
        newTariff.setEffectiveDate(effectiveDate);
        newTariff.setExpiryDate(expiryDate);
        newTariff.setReference(reference);

        // save tariff
        tariffRepository.save(newTariff);

        // validate if theres too many tariffrates, <= 3
        if (createDto.getTariffRates().size() > 3) {
            throw new WrongNumberOfArgumentsException("Cannot have more than 3 tariff rates.");
        }


        // create and set TariffRates
        List<TariffRate> tariffRateList = new ArrayList<>();
        for (Map.Entry<UnitOfCalculation, BigDecimal> entry : createDto.getTariffRates().entrySet()) {
            UnitOfCalculation unitOfCalculation = entry.getKey();
            BigDecimal rate = entry.getValue();

            TariffRate tariffRate = new TariffRate();

            // map tariffid
            tariffRate.setTariff(newTariff);

            tariffRate.setUnitOfCalculation(unitOfCalculation);

            tariffRate.setTariffRate(rate);

            tariffRateRepository.save(tariffRate);
            tariffRateList.add(tariffRate);
        }
        return tariffRateList;
    }

    @Override
    public List<TariffDto> getAllTariffRates() {
        // group tariffrates by tariff ID
        Map<Long, List<TariffRate>> groupedRates = tariffRateRepository.findAll().stream()
                .collect(Collectors.groupingBy(tr -> tr.getTariff().getTariffID()));
        
        return groupedRates.values().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TariffDto> getTariffById(Long id) {
        List<TariffRate> tariffRates = tariffRateRepository.findAllByTariff_TariffID(id);
        if (tariffRates.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(convertToDto(tariffRates));
    }

    // @Override
    // public List<Tariff> getTariffRatesByCountries(String country, Integer hsCode, Date tradeDate) {
    //     String importer = "USA";
    //     String exporter = country;
    //     TariffMapping tariffMapping = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(hsCode, importer, exporter);
        
    //     if (tariffMapping == null) {
    //         throw new ResourceNotFoundException(String.format("No tariff was found. HSCode: %d, Importer: %s, Exporter: %s", hsCode, importer, exporter));
    //     }
    //     return tariffRepository.findValidTariffs(tariffMapping, tradeDate);
    // }

    @Override
    public List<TariffDto> getTariffsByPage(int page, int pageSize) {
        Map<Long, List<TariffRate>> groupedRates = tariffRateRepository.findAll().stream()
                .collect(Collectors.groupingBy(tr -> tr.getTariff().getTariffID()));
        
        return groupedRates.values().stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TariffDto createTariff(TariffCreateDto createDto) {
        // Validate business rules
        validateTariffBusinessRules(createDto);

        List<TariffRate> entities = convertToEntity(createDto);
        return convertToDto(entities);
    }
    
    /**
     * Validates business rules for tariff creation
     */
    private void validateTariffBusinessRules(TariffCreateDto createDto) {
        // Rule 1: Exporter and importer cannot be the same
        if (createDto.getExporter().equals(createDto.getImporter())) {
            throw new SameCountryException(createDto.getExporter());
        }

        // Rule 2: At least one tariff rate is required
        if (createDto.getTariffRates() == null || createDto.getTariffRates().isEmpty()) {
            throw new IllegalArgumentException("At least one tariff rate is required");
        }

        // Rule 3: Rate cannot be negative
        createDto.getTariffRates().forEach((unit, rate) -> {
            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                throw new NegativeTariffRateException("Tariff rate cannot be negative: " + rate);
            }
        });
        
        // Rule 4: Expiry date must be after effective date
        if (createDto.getEffectiveDate() != null && createDto.getExpiryDate() != null) {
            if (createDto.getExpiryDate().before(createDto.getEffectiveDate())) {
                throw new ExpiryBeforeEffectiveException("Expiry date must be after effective date");
            }
        }
    }
    


    private Tariff updateTariff(Long id, TariffCreateDto updateDto) {
        Tariff tariff = tariffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", id.toString()));


        // ensure that exporter, importer, product cannot be changed, only dates and reference
        if (!tariff.getCountryPair().getExporter().getIsoCode().equals(updateDto.getExporter()) ||
            !tariff.getCountryPair().getImporter().getIsoCode().equals(updateDto.getImporter()) ||
            !tariff.getProduct().getHSCode().equals(updateDto.getHSCode())) {
            throw new ImmutableFieldChangeException("Exporter, Importer, and Product cannot be changed, please create a new tariff instead.");
        }

        if (updateDto.getEffectiveDate() != null) {
            tariff.setEffectiveDate(new Date(updateDto.getEffectiveDate().getTime()));
        }

        if (updateDto.getExpiryDate() != null) {
            tariff.setExpiryDate(new Date(updateDto.getExpiryDate().getTime()));
        }
        // set reference regardless, because can be blank also
        tariff.setReference(updateDto.getReference());

        // save and cascade to all tariff rates
        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public TariffDto updateTariffRate(Long id, TariffCreateDto updateDto) {
        List<TariffRate> existingList = tariffRateRepository.findAllByTariff_TariffID(id);
        if (existingList.isEmpty()) {
            throw new ResourceNotFoundException("TariffRate", id.toString());
        }
        Tariff updatedTariff = updateTariff(id, updateDto);
        existingList.forEach(existing -> existing.setTariff(updatedTariff));

        
        // update tariff rate if provided, only allow to change one
        Map<UnitOfCalculation, BigDecimal> newRates = updateDto.getTariffRates();

        // if size of list and map is different, throw error
        if (existingList.size() != newRates.size()) {
            throw new WrongNumberOfArgumentsException("Number of tariff rates must remain the same.");
        }

        // look through the map and update each tariff rate big decimal accordingly, no update of unitofcalculation
        for (TariffRate existing : existingList) {
            UnitOfCalculation uoc = existing.getUnitOfCalculation();
            if (newRates.containsKey(uoc)) {
                BigDecimal newRate = newRates.get(uoc);
                if (newRate.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NegativeTariffRateException("Tariff rate cannot be negative: " + newRate);
                }
                existing.setTariffRate(newRate);
            } else {
                throw new ResourceNotFoundException("No tariff rate found for unit: " + uoc);
            }

           tariffRateRepository.save(existing);
        }

        return convertToDto(existingList);
    }

    @Override
    @Transactional
    public void deleteTariff(Long id) {
        if (!tariffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tariff", id);
        }
        
        tariffRepository.deleteById(id);
    }

    

    @Override
    public List<TariffDto> getTariffsByHSCode(String hsCode) {
        Map<Long, List<TariffRate>> groupedRates = tariffRateRepository.findAll().stream()
                .filter(tariffRate -> tariffRate.getTariff().getProduct().getHSCode().equals(hsCode))
                .collect(Collectors.groupingBy(tr -> tr.getTariff().getTariffID()));
        
        return groupedRates.values().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CalculationResult calculateTariff(CalculationRequest calculationDto) {
        // fetch tariff

        CountryPair countryPair = countryPairRepository.findSingleByExporterAndImporter(
            calculationDto.getExporter(), calculationDto.getImporter()); 
        
        Optional<Tariff> tariffOpt = tariffRepository.findValidTariff(
            calculationDto.getHSCode(),
            countryPair,
            calculationDto.getTradeDate()
        );
        Tariff tariff = tariffOpt.orElseThrow(() ->
            new ResourceNotFoundException("No such tariff record found")
        );

        // fetch tariffrates
        Long tariffId = tariff.getTariffID();
        List<TariffRate> tariffRates = tariffRateRepository.findAllByTariff_TariffID(tariffId);
        
        // format mapping of qty - rate - value
        List<TariffCalculationMap> tariffList = new ArrayList<>();
        for (TariffRate tRate : tariffRates) {
            UnitOfCalculation unitOfCalculation = tRate.getUnitOfCalculation();
            BigDecimal rate = tRate.getTariffRate();
            BigDecimal value = calculationDto.getQuantityValues().get(unitOfCalculation);

            // if Ad Valorem rate required, use productValue as value
            if (unitOfCalculation.equals(UnitOfCalculation.AV)) {value = calculationDto.getProductValue();}

            TariffCalculationMap tariffMap = new TariffCalculationMap(unitOfCalculation, rate, value);

            tariffList.add(tariffMap);
        }

        // tariff calculation handler
        CalculationResult cr = tariffCalculationService.calculate(tariffList, calculationDto.getProductValue());

        // set other tariff info
        cr.setEffectiveDate(tariff.getEffectiveDate());
        cr.setExpiryDate(tariff.getExpiryDate());
        cr.setReference(tariff.getReference());
        return cr;
    }
}
