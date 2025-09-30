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
    private final ProductMetricRepository productMetricRepository;
    private final TariffCalculationService tariffCalculationService; 

    public TariffServiceImpl(TariffRepository tariffRepository, TariffRateRepository tariffRateRepository, 
    CountryRepository countryRepository, ProductRepository productRepository, TariffCalculationService tariffCalculationService, 
    BusinessExceptionHandler businessExceptionHandler, ProductMetricRepository productMetricRepository) {
        this.tariffRepository = tariffRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
        this.tariffCalculationService = tariffCalculationService;
        this.businessExceptionHandler = businessExceptionHandler;
        this.productMetricRepository = productMetricRepository;
    }

    private TariffDto convertToDto(TariffRate tariffRate) {
        
        TariffDto dto = new TariffDto();

        Tariff tariff = tariffRate.getTariff();

        dto.setTariffID(tariff.getTariffID());
        dto.setTariffName(tariff.getTariffName());

        // product details
        dto.sethSCode(tariff.getProduct().getHSCode());
        dto.setProductDescription(tariff.getProduct().getDescription());
        // exporter details
        dto.setExporterCode(tariff.getExporter().getIsoCode());
        dto.setExporterName(tariff.getExporter().getName());

        // tariff details
        dto.setEffectiveDate(tariff.getEffectiveDate());
        dto.setExpiryDate(tariff.getExpiryDate());
        dto.setReference(tariff.getReference());  

        // tariff rate details - assume only one rate per tariff for now
        dto.setTariffRateID(tariffRate.getTariffRateID());
        dto.setUnitOfCalculation(tariffRate.getProductMetric().getUnitOfCalculation());
        dto.setTariffRate(tariffRate.getTariffRate());
        return dto;
    }

    private TariffRate convertToEntity(TariffCreateDto createDto) {

        String name = createDto.getName();

        Product product = productRepository.findById(createDto.gethSCode())
            .orElseThrow(() -> new ResourceNotFoundException("Product", createDto.gethSCode().toString()));

        Country exporter = countryRepository.findById(createDto.getExporter())
            .orElseThrow(() -> new ResourceNotFoundException("Country", createDto.getExporter()));
        
        String reference = createDto.getReference();

        // Convert java.util.Date to java.sql.Date
        Date effectiveDate = createDto.getEffectiveDate() != null ? 
            new Date(createDto.getEffectiveDate().getTime()) : null;
        Date expiryDate = createDto.getExpiryDate() != null ? 
            new Date(createDto.getExpiryDate().getTime()) : null;

        Tariff newTariff = new Tariff();

        newTariff.setTariffName(name);
        newTariff.setProduct(product);
        newTariff.setExporter(exporter);
        newTariff.setEffectiveDate(effectiveDate);
        newTariff.setExpiryDate(expiryDate);
        newTariff.setReference(reference);

        // create and set TariffRate
        TariffRate tariffRate = new TariffRate();

        // map tariffid
        tariffRate.setTariff(newTariff);

        // check whether product metric exists before setting tariff rate to it
        ProductMetric existingMetric = productMetricRepository.findByProductAndUnitOfCalculation(product, createDto.getUnitOfCalculation());
        if (existingMetric != null) {
            tariffRate.setProductMetric(existingMetric);
        } else {
            ProductMetric productMetric = new ProductMetric();
            productMetric.setProduct(product);
            productMetric.setUnitOfCalculation(createDto.getUnitOfCalculation());
            
            // save new product metric
            productMetricRepository.save(productMetric);
            tariffRate.setProductMetric(productMetric);
        }
        
        // update tariff rate
        tariffRate.setTariffRate(createDto.getTariffRate());

        // save tariff
        tariffRepository.save(newTariff);
        return tariffRate;
    }

    @Override
    public List<List<TariffDto>> getAllTariffRates() {

        // group tariffrates by tariffID
        List<Long> tariffIds = tariffRateRepository.findAll().stream()
                .map(tr -> tr.getTariff().getTariffID())
                .distinct()
                .collect(Collectors.toList());
        return tariffIds.stream()
                .map(id -> tariffRateRepository.findAllByTariff_TariffID(id).stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TariffDto> getTariffById(Long id) {
        return tariffRateRepository.findAllByTariff_TariffID(id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // @Override
    // public List<Tariff> getTariffRatesByCountries(String country, String tradeDirection, Integer hsCode, Date tradeDate) {
    //     String importer = tradeDirection.equals("import") ? country : "USA";
    //     String exporter = tradeDirection.equals("export") ? country : "USA";
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
        // Rule 1: Expiry date must be after effective date
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

        if (updateDto.getTariffCreateDto().getName() != null) {
            tariff.setTariffName(updateDto.getTariffCreateDto().getName());
        }

        if (updateDto.getTariffCreateDto().gethSCode() != null) {
            Product product = productRepository.findById(updateDto.getTariffCreateDto().gethSCode())
                .orElseThrow(() -> new ResourceNotFoundException("Product", updateDto.getTariffCreateDto().gethSCode().toString()));
            tariff.setProduct(product);
        }

        if (updateDto.getTariffCreateDto().getExporter() != null) {
            Country exporter = countryRepository.findById(updateDto.getTariffCreateDto().getExporter())
                .orElseThrow(() -> new ResourceNotFoundException("Country", updateDto.getTariffCreateDto().getExporter()));
            tariff.setExporter(exporter);
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
            ProductMetric productMetric = productMetricRepository.findByProductAndUnitOfCalculation(
                updatedTariff.getProduct(), updateDto.getTariffCreateDto().getUnitOfCalculation());
            if (productMetric != null) {
                existing.setProductMetric(productMetric);
            }
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
                .filter(tariffRate -> tariffRate.getProductMetric().getProduct().getHSCode().equals(hsCode))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CalculationResult calculateTariff(CalculationRequest calculationDto) {
        // fetch tariff
        Optional<Tariff> tariffOpt = tariffRepository.findValidTariff(
            calculationDto.gethSCode(),
            calculationDto.getExporter(),
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
            UnitOfCalculation unitOfCalculation = tRate.getProductMetric().getUnitOfCalculation();
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
