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
    private final TariffCalculationService tariffCalculationService; 

    public TariffServiceImpl(TariffRepository tariffRepository, TariffRateRepository tariffRateRepository, CountryRepository countryRepository, ProductRepository productRepository, TariffCalculationService tariffCalculationService, BusinessExceptionHandler businessExceptionHandler) {
        this.tariffRepository = tariffRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
        this.tariffCalculationService = tariffCalculationService;
        this.businessExceptionHandler = businessExceptionHandler;
    }

    private TariffDto convertToDto(Tariff tariff) {
        TariffDto dto = new TariffDto();

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
        return dto;
    }

    private Tariff convertToEntity(TariffCreateDto createDto) {

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

        return newTariff;
    }

    @Override
    public List<TariffDto> getAllTariffs() {
        return tariffRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TariffDto getTariffById(Long id) {
        Tariff tariff = tariffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", id));
        return convertToDto(tariff);
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
        return tariffRepository.findAll().stream()
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
        
        Tariff entity = convertToEntity(createDto);
        Tariff saved = tariffRepository.save(entity);
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

    @Override
    @Transactional
    public TariffDto updateTariff(Long id, TariffCreateDto createDto) {
        // Find the existing tariff
        Tariff existing = tariffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", id.toString()));
        
        // Important: Preserve the existing tariff mapping relationship
        // This ensures tariff_mapping_id never changes during updates
        Tariff updatedTariff = convertToEntity(createDto);

        // update name
        existing.setTariffName(updatedTariff.getTariffName());

        // update exporter
        existing.setExporter(updatedTariff.getExporter());

        // update product
        existing.setProduct(updatedTariff.getProduct());
        
        // Only update the dates and reference
        if (updatedTariff.getEffectiveDate() != null) {
            existing.setEffectiveDate(updatedTariff.getEffectiveDate());
        }
        
        if (updatedTariff.getExpiryDate() != null) {
            existing.setExpiryDate(updatedTariff.getExpiryDate());
        }
        
        // Reference can be null or updated
        if (updatedTariff.getReference() == null && existing.getReference() == null) {
        } else {
            existing.setReference(updatedTariff.getReference());
        }
        
        // Validate tariff business rules (dates, rates, etc.)
        validateTariffUpdateRules(existing);
        
        // Save and return
        Tariff updated = tariffRepository.save(existing);
        return convertToDto(updated);
    }

    /**
     * Validates business rules for tariff updates
     */
    private void validateTariffUpdateRules(Tariff tariff) {
        // // Rule 1: Rate cannot be negative
        // if (tariff.getRate().compareTo(BigDecimal.ZERO) < 0) {
        //     throw new NegativeTariffRateException("Tariff rate cannot be negative: " + tariff.getRate());
        // }
        
        // Rule 2: Expiry date must be after effective date
        if (tariff.getEffectiveDate() != null && tariff.getExpiryDate() != null 
                && tariff.getExpiryDate().before(tariff.getEffectiveDate())) {
            throw new ExpiryBeforeEffectiveException("Expiry date must be after effective date");
        }
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
        return tariffRepository.findAll().stream()
                .filter(tariff -> tariff.getProduct().getHSCode().equals(hsCode))
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
