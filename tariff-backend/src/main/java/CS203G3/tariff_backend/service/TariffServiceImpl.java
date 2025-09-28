package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import CS203G3.tariff_backend.dto.*;
import CS203G3.tariff_backend.exception.*;
import CS203G3.tariff_backend.exception.tariff.*;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of TariffService with DTO support
 */
@Service
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMappingRepository tariffMappingRepository;
    private final TariffMappingService tariffMappingService;

    public TariffServiceImpl(TariffRepository tariffRepository, TariffMappingRepository tariffMappingRepository, TariffMappingService tariffMappingService) {
        this.tariffRepository = tariffRepository;
        this.tariffMappingRepository = tariffMappingRepository;
        this.tariffMappingService = tariffMappingService;
    }

    private TariffDto convertToDto(Tariff tariff) {
        TariffDto dto = new TariffDto();
        dto.setTariffID(tariff.getTariffID());
        dto.setTariffMappingID(tariff.getTariffMapping().getTariffMappingID());
        dto.setRate(tariff.getRate());
        dto.setEffectiveDate(tariff.getEffectiveDate());
        dto.setExpiryDate(tariff.getExpiryDate());
        dto.setReference(tariff.getReference());
        
        // Add mapping details for frontend display
        TariffMapping mapping = tariff.getTariffMapping();
        dto.setExporterCode(mapping.getExporter().getIsoCode());
        dto.setExporterName(mapping.getExporter().getName());
        dto.setImporterCode(mapping.getImporter().getIsoCode());
        dto.setImporterName(mapping.getImporter().getName());
        dto.setHSCode(mapping.getProduct().getHsCode());
        dto.setProductDescription(mapping.getProduct().getDescription());
        
        return dto;
    }

    private Tariff convertToEntity(TariffCreateDto createDto) {
        // Find existing mapping or create a new one
        TariffMapping mapping = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(
            createDto.getHSCode(), 
            "USA", 
            createDto.getExporter()
        );
        
        // If mapping doesn't exist, create it using the service
        if (mapping == null) {
            TariffMappingCreateDto mappingDto = new TariffMappingCreateDto();
            mappingDto.setHSCode(createDto.getHSCode());
            mappingDto.setImporter("USA");
            mappingDto.setExporter(createDto.getExporter());
            
            TariffMappingDto createdMappingDto = tariffMappingService.createTariffMapping(mappingDto);
            mapping = tariffMappingRepository.findById(createdMappingDto.getTariffMappingID())
                .orElseThrow(() -> new InvalidOperationException("Failed to create tariff mapping"));
        }
        
        // Convert java.util.Date to java.sql.Date
        Date effectiveDate = createDto.getEffectiveDate() != null ? 
            new Date(createDto.getEffectiveDate().getTime()) : null;
        Date expiryDate = createDto.getExpiryDate() != null ? 
            new Date(createDto.getExpiryDate().getTime()) : null;
            
        return new Tariff(mapping, createDto.getRate(), effectiveDate, expiryDate, createDto.getReference());
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

    @Override
    public List<Tariff> getTariffRatesByCountries(String country, Integer hsCode, Date tradeDate) {
        String importer = "USA";
        String exporter = country;
        TariffMapping tariffMapping = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(hsCode, importer, exporter);
        
        if (tariffMapping == null) {
            throw new ResourceNotFoundException(String.format("No tariff was found. HSCode: %d, Importer: %s, Exporter: %s", hsCode, importer, exporter));
        }
        return tariffRepository.findValidTariffs(tariffMapping, tradeDate);
    }

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
        // Rule 1: Exporter and importer cannot be the same
        if (createDto.getExporter() != null && 
            createDto.getExporter().equals("USA")) {
            throw new SameCountryException(createDto.getExporter());
        }
        
        // Rule 2: Rate cannot be negative
        if (createDto.getRate() != null && createDto.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeTariffRateException("Tariff rate cannot be negative: " + createDto.getRate());
        }
        
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
        validateNoOverlappingTariffs(createDto);
    }
    
    /**
     * Validates that no overlapping tariffs exist for the same tariff mapping
     */
    private void validateNoOverlappingTariffs(TariffCreateDto createDto) {
        // Find existing tariff mapping
        TariffMapping existingMapping = tariffMappingRepository
            .findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(
                createDto.getHSCode(), 
                "USA", 
                createDto.getExporter()
            );
        
        if (existingMapping != null) {
            // Check for overlapping tariffs
            List<Tariff> overlappingTariffs = tariffRepository
                .findOverlappingTariffs(
                    existingMapping,
                    new Date(createDto.getEffectiveDate().getTime()),
                    createDto.getExpiryDate() != null ? new Date(createDto.getExpiryDate().getTime()) : null
                );
            
            if (!overlappingTariffs.isEmpty()) {
                throw new OverlappingTariffPeriodException(
                    String.format("Overlapping tariff period found for %s -> %s, HSCode: %d", 
                        createDto.getExporter(), "USA", createDto.getHSCode())
                );
            }
        }
    }

    @Override
    @Transactional
    public TariffDto updateTariff(Long id, TariffCreateDto createDto) {
        // Find the existing tariff
        Tariff existing = tariffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", id.toString()));
        
        // Important: Preserve the existing tariff mapping relationship
        // This ensures tariff_mapping_id never changes during updates
        Tariff updatedTariff = convertToEntity(createDto);

        // Only update the rate, dates and reference
        if (updatedTariff.getRate() != null) {
            existing.setRate(updatedTariff.getRate());
        }
        
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
        // Rule 1: Rate cannot be negative
        if (tariff.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeTariffRateException("Tariff rate cannot be negative: " + tariff.getRate());
        }
        
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
    public List<TariffDto> getTariffsByMappingId(Long tariffMappingId) {
        return tariffRepository.findAll().stream()
                .filter(tariff -> tariff.getTariffMapping().getTariffMappingID().equals(tariffMappingId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
