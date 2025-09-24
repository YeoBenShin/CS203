package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import CS203G3.tariff_backend.dto.*;
import CS203G3.tariff_backend.exception.*;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

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
            createDto.getImporter(), 
            createDto.getExporter()
        );
        
        // If mapping doesn't exist, create it using the service
        if (mapping == null) {
            TariffMappingCreateDto mappingDto = new TariffMappingCreateDto();
            mappingDto.setHSCode(createDto.getHSCode());
            mappingDto.setImporter(createDto.getImporter());
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
    public List<Tariff> getTariffRatesByCountries(String country, String tradeDirection, Integer hsCode, Date tradeDate) {
        String importer = tradeDirection.equals("import") ? country : "USA";
        String exporter = tradeDirection.equals("export") ? country : "USA";
        TariffMapping tariffMapping = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(hsCode, importer, exporter);
        
        if (tariffMapping == null) {
            throw new ResourceNotFoundException("TariffMapping", String.format("HSCode: %d, Importer: %s, Exporter: %s", hsCode, importer, exporter));
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
        Tariff entity = convertToEntity(createDto);
        Tariff saved = tariffRepository.save(entity);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public TariffDto updateTariff(Long id, TariffCreateDto createDto) {
        Tariff existing = tariffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tariff", id));

        TariffMapping mapping = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(
            createDto.getHSCode(),
            createDto.getImporter(),
            createDto.getExporter());
        
        if (mapping == null) {
            throw new ResourceNotFoundException("TariffMapping", String.format("HSCode: %d, Importer: %s, Exporter: %s", 
                createDto.getHSCode(), createDto.getImporter(), createDto.getExporter()));
        }

        // Convert dates
        Date effectiveDate = createDto.getEffectiveDate() != null ? 
            new Date(createDto.getEffectiveDate().getTime()) : null;
        Date expiryDate = createDto.getExpiryDate() != null ? 
            new Date(createDto.getExpiryDate().getTime()) : null;
        
        existing.setTariffMapping(mapping);
        existing.setRate(createDto.getRate());
        existing.setEffectiveDate(effectiveDate);
        existing.setExpiryDate(expiryDate);
        existing.setReference(createDto.getReference());
        
        Tariff updated = tariffRepository.save(existing);
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
    public List<TariffDto> getTariffsByMappingId(Long tariffMappingId) {
        return tariffRepository.findAll().stream()
                .filter(tariff -> tariff.getTariffMapping().getTariffMappingID().equals(tariffMappingId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
