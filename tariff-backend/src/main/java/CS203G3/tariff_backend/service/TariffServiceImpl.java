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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * Implementation of TariffService with DTO support
 */
@Service
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMappingRepository tariffMappingRepository;

    public TariffServiceImpl(TariffRepository tariffRepository, TariffMappingRepository tariffMappingRepository) {
        this.tariffRepository = tariffRepository;
        this.tariffMappingRepository = tariffMappingRepository;
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
        TariffMapping mapping = tariffMappingRepository.findById(createDto.getTariffMappingID())
            .orElseThrow(() -> new ResourceNotFoundException("TariffMapping", createDto.getTariffMappingID()));
        
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
    public List<TariffDto> getTariffsByPage(int page, int pageSize) {
        Page<Tariff> tariffPage = tariffRepository.findAll(
            PageRequest.of(page-1, pageSize, Sort.by("TariffID").ascending())
        );
        return tariffPage.getContent().stream()
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
        
        TariffMapping mapping = tariffMappingRepository.findById(createDto.getTariffMappingID())
            .orElseThrow(() -> new ResourceNotFoundException("TariffMapping", createDto.getTariffMappingID()));

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
