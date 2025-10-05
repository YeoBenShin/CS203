package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import CS203G3.tariff_backend.dto.*;
import CS203G3.tariff_backend.exception.*;
import CS203G3.tariff_backend.exception.tariff.*;

import java.sql.Date;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of TariffService with DTO support
 */
@Service
public class TariffServiceImpl implements TariffService {
    private final BusinessExceptionHandler businessExceptionHandler;
    private final TariffRateRepository tariffRateRepository;
    private final TariffRepository tariffRepository;
    private final CountryRepository countryRepository;
    private final ProductRepository productRepository;  
    private final CountryPairRepository countryPairRepository;
    private final TariffCalculationService tariffCalculationService; 

    public TariffServiceImpl(TariffRepository tariffRepository, TariffRateRepository tariffRateRepository, 
    CountryRepository countryRepository, ProductRepository productRepository, TariffCalculationService tariffCalculationService, 
    BusinessExceptionHandler businessExceptionHandler, CountryPairRepository countryPairRepository) {
        this.tariffRepository = tariffRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
        this.tariffCalculationService = tariffCalculationService;
        this.businessExceptionHandler = businessExceptionHandler;
        this.countryPairRepository = countryPairRepository;
    }

    private TariffDto convertToDto(TariffRate tariffRate) {
        
        TariffDto dto = new TariffDto();

        Tariff tariff = tariffRate.getTariff();

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

        // tariff rate details - assume only one rate per tariff for now
        dto.setTariffRateID(tariffRate.getTariffRateID());
        dto.setUnitOfCalculation(tariffRate.getUnitOfCalculation());
        dto.setTariffRate(tariffRate.getTariffRate());
        return dto;
    }

    private TariffRate convertToEntity(TariffCreateDto createDto) {

        Product product = productRepository.findById(createDto.getHSCode())
            .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.getHSCode()));

        String exporterCode = createDto.getExporter();
        String importerCode = createDto.getImporter();

        CountryPair countryPair = countryPairRepository.findByExporterAndImporter(exporterCode, importerCode);

        if (countryPair == null) {
            countryPair = new CountryPair();
            countryPair.setExporter(countryRepository.findById(exporterCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country", exporterCode)));
            countryPair.setImporter(countryRepository.findById(importerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country", importerCode)));

            countryPairRepository.save(countryPair);
        }

        UnitOfCalculation unitOfCalculation = createDto.getUnitOfCalculation();

        if (unitOfCalculation == null) {
            unitOfCalculation = UnitOfCalculation.AV; // default to Ad Valorem if not specified
        }

        String reference = createDto.getReference();

        // Convert java.util.Date to java.sql.Date
        Date effectiveDate = createDto.getEffectiveDate() != null ? 
            new Date(createDto.getEffectiveDate().getTime()) : null;
        Date expiryDate = createDto.getExpiryDate() != null ? 
            new Date(createDto.getExpiryDate().getTime()) : null;

        // find whether tariff witih same 
        Tariff newTariff = new Tariff();

        newTariff.setProduct(product);
        newTariff.setCountryPair(countryPair);
        newTariff.setEffectiveDate(effectiveDate);
        newTariff.setExpiryDate(expiryDate);
        newTariff.setReference(reference);

        // create and set TariffRate
        TariffRate tariffRate = new TariffRate();

        // map tariffid
        tariffRate.setTariff(newTariff);

        // check whether product metric exists before setting tariff rate to it
        tariffRate.setUnitOfCalculation(unitOfCalculation);
        
        // update tariff rate
        tariffRate.setTariffRate(createDto.getTariffRate());

        // save tariff
        tariffRepository.save(newTariff);
        return tariffRate;
    }

    @Override
    public List<TariffDto> getAllTariffRates() {
        // group tariffrates by country pair and product
        return tariffRateRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TariffDto> getTariffById(Long id) {
        return tariffRateRepository.findAllByTariff_TariffID(id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
        return tariffRateRepository.findAll().stream()
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

        TariffRate entity = convertToEntity(createDto);
        TariffRate saved = tariffRateRepository.save(entity);
        return convertToDto(saved);
    }
    
    /**
     * Validates business rules for tariff creation
     */
    private void validateTariffBusinessRules(TariffCreateDto createDto) {
        // Rule 1: Exporter and importer cannot be the same
        if (createDto.getExporter() != null && 
            createDto.getExporter().equals("USA")) {
            throw new SameCountryException(createDto.getExporter());
        }
        
        // Rule 2: Rate cannot be negative
        // if (createDto.getRate() != null && createDto.getRate().compareTo(BigDecimal.ZERO) < 0) {
        //     throw new NegativeTariffRateException("Tariff rate cannot be negative: " + createDto.getRate());
        // }
        
        // Rule 3: Rate validation removed - allowing any positive rate
        // (No upper limit on tariff rates as they can vary widely in real-world scenarios)
        
        // // Rule 4: Effective date cannot be in the past (optional - depends on business needs)
        // if (createDto.getEffectiveDate() != null) {
        //     LocalDate effectiveDate = new java.sql.Date(createDto.getEffectiveDate().getTime()).toLocalDate();
        //     LocalDate today = LocalDate.now();
        //     if (effectiveDate.isBefore(today)) {
        //         throw new PastEffectiveDateException("Effective date cannot be in the past: " + effectiveDate);
        //     }
        // }
        
        // Rule 5: Expiry date must be after effective date
        if (createDto.getEffectiveDate() != null && createDto.getExpiryDate() != null) {
            if (createDto.getExpiryDate().before(createDto.getEffectiveDate())) {
                throw new ExpiryBeforeEffectiveException("Expiry date must be after effective date");
            }
        }
        
        // Rule 6: Check for overlapping tariff periods (same mapping, overlapping dates)
        // validateNoOverlappingTariffs(createDto);
    }
    
    /**
     * Validates that no overlapping tariffs exist for the same tariff mapping
     */
    // private void validateNoOverlappingTariffs(TariffCreateDto createDto) {
    //     // Find existing tariff mapping
    
    //         // Check for overlapping tariffs
    //         List<Tariff> overlappingTariffs = tariffRepository
    //             .findOverlappingTariffs(
    //                 existingMapping,
    //                 new Date(createDto.getEffectiveDate().getTime()),
    //                 createDto.getExpiryDate() != null ? new Date(createDto.getExpiryDate().getTime()) : null
    //             );
            
    //         if (!overlappingTariffs.isEmpty()) {
    //             throw new OverlappingTariffPeriodException(
    //                 String.format("Overlapping tariff period found for %s -> %s, HSCode: %d", 
    //                     createDto.getExporter(), createDto.getImporter(), createDto.getHSCode())
    //             );
    //         }
    //     }
    // }


    private Tariff updateTariff(TariffUpdateDto updateDto) {
        Tariff tariff = tariffRepository.findById(updateDto.getTariffID())
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", updateDto.getTariffID().toString()));


        if (updateDto.getTariffCreateDto().getHSCode() != null) {
            Product product = productRepository.findById(updateDto.getTariffCreateDto().getHSCode())
                .orElseThrow(() -> new ResourceNotFoundException("Product", updateDto.getTariffCreateDto().getHSCode()));
            tariff.setProduct(product);
        }

        // update country pair by checking importer and exporter
        if (updateDto.getTariffCreateDto().getExporter() != null && updateDto.getTariffCreateDto().getImporter() != null) {
            String exporterCode = updateDto.getTariffCreateDto().getExporter();
            String importerCode = updateDto.getTariffCreateDto().getImporter();

            CountryPair countryPair = countryPairRepository.findByExporterAndImporter(exporterCode, importerCode);

            if (countryPair == null) {
                countryPair = new CountryPair();
                countryPair.setExporter(countryRepository.findById(exporterCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Country", exporterCode)));
                countryPair.setImporter(countryRepository.findById(importerCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Country", importerCode)));

                countryPairRepository.save(countryPair);
            }

            tariff.setCountryPair(countryPair);
        }

        if (updateDto.getTariffCreateDto().getEffectiveDate() != null) {
            tariff.setEffectiveDate(new Date(updateDto.getTariffCreateDto().getEffectiveDate().getTime()));
        }

        if (updateDto.getTariffCreateDto().getExpiryDate() != null) {
            tariff.setExpiryDate(new Date(updateDto.getTariffCreateDto().getExpiryDate().getTime()));
        }

        if (updateDto.getTariffCreateDto().getReference() != null) {
            tariff.setReference(updateDto.getTariffCreateDto().getReference());
        }

        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public TariffDto updateTariffRate(Long id, TariffUpdateDto updateDto) {
        TariffRate existing = tariffRateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TariffRate", id.toString()));

        Tariff updatedTariff = updateTariff(updateDto);
        existing.setTariff(updatedTariff);

        if (updateDto.getTariffCreateDto().getTariffRate() != null) {
            existing.setTariffRate(updateDto.getTariffCreateDto().getTariffRate());
        }

        if (updateDto.getTariffCreateDto().getUnitOfCalculation() != null) {
            existing.setUnitOfCalculation(updateDto.getTariffCreateDto().getUnitOfCalculation());
        }

        TariffRate updated = tariffRateRepository.save(existing);
        return convertToDto(updated);
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
        return tariffRateRepository.findAll().stream()
                .filter(tariffRate -> tariffRate.getTariff().getProduct().getHSCode().equals(hsCode))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CalculationResult calculateTariff(CalculationRequest calculationDto) {
        // fetch tariff

        CountryPair countryPair = countryPairRepository.findByExporterAndImporter(
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
        cr.setTariffName(tariff.getTariffName());
        cr.setEffectiveDate(tariff.getEffectiveDate());
        cr.setExpiryDate(tariff.getExpiryDate());
        cr.setReference(tariff.getReference());
        return cr;
    }
}
