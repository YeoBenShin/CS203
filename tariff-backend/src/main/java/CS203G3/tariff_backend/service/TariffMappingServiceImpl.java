package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.*;
import CS203G3.tariff_backend.repository.*;
import CS203G3.tariff_backend.dto.*;
import CS203G3.tariff_backend.exception.CountryNotFoundException;
import CS203G3.tariff_backend.exception.ProductNotFoundException;
import CS203G3.tariff_backend.exception.TariffMappingNotFoundException;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TariffMappingServiceImpl implements TariffMappingService {

    private final ProductRepository productRepository;
    private final CountryRepository countryRepository;
    private final TariffMappingRepository tariffMappingRepository;

    @Autowired
    public TariffMappingServiceImpl(ProductRepository productRepository, CountryRepository countryRepository, TariffMappingRepository tariffMappingRepository) {
        this.productRepository = productRepository;
        this.countryRepository = countryRepository;
        this.tariffMappingRepository = tariffMappingRepository;
    }

    public TariffMappingDto convertToDto(TariffMapping tariffMapping) {
        TariffMappingDto dto = new TariffMappingDto();
        dto.setTariffMappingID(tariffMapping.getTariffMappingID());
        dto.setProductId(tariffMapping.getProduct().getHsCode());
        dto.setImporter(tariffMapping.getImporter().getIsoCode());
        dto.setExporter(tariffMapping.getExporter().getIsoCode());
        return dto;
    }

    public TariffMapping convertToEntity(TariffMappingCreateDto dto) {
        TariffMapping tariffMapping = new TariffMapping();
        tariffMapping.setProduct(productRepository.findById(dto.getProductId()).orElseThrow(() -> new ProductNotFoundException(dto.getProductId())));
        tariffMapping.setImporter(countryRepository.findById(dto.getImporter()).orElseThrow(() -> new CountryNotFoundException(dto.getImporter())));
        tariffMapping.setExporter(countryRepository.findById(dto.getExporter()).orElseThrow(() -> new CountryNotFoundException(dto.getExporter())));
        return tariffMapping;
    }

    @Override
    @Transactional
    public TariffMappingDto createTariffMapping(TariffMappingCreateDto tariffMappingCreateDto) {
        TariffMapping entity = convertToEntity(tariffMappingCreateDto);
        TariffMapping saved = tariffMappingRepository.save(entity);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public void deleteTariffMapping(Long id) throws TariffMappingNotFoundException {
        if (!tariffMappingRepository.existsById(id)) {
            throw new TariffMappingNotFoundException(id);
        }
        
        tariffMappingRepository.deleteById(id);
    }

    @Override
    public List<TariffMappingDto> getAllTariffMappings() {
        List<TariffMapping> tariffMappings = tariffMappingRepository.findAll();

        return tariffMappings.stream().map(this::convertToDto).toList();
    }

    @Override
    public TariffMappingDto getTariffMappingById(Long id) throws TariffMappingNotFoundException {
        
        TariffMapping entity = tariffMappingRepository.findById(id)
                .orElseThrow(() -> new TariffMappingNotFoundException(id));

        return convertToDto(entity);
    }

    @Override
    @Transactional
    public TariffMapping updateTariffMapping(Long id, TariffMappingDto tariffMappingDto)
            throws TariffMappingNotFoundException {
        if (!tariffMappingRepository.existsById(id)) {
            throw new TariffMappingNotFoundException(id);
        }

        TariffMapping tariffMapping = tariffMappingRepository.findById(id)
                .orElseThrow(() -> new TariffMappingNotFoundException(id));

        tariffMapping.setProduct(productRepository.findById(tariffMappingDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + tariffMappingDto.getProductId())));
        
        tariffMapping.setImporter(countryRepository.findById(tariffMappingDto.getImporter())
                .orElseThrow(() -> new IllegalArgumentException("Importer country not found: " + tariffMappingDto.getImporter())));

        tariffMapping.setExporter(countryRepository.findById(tariffMappingDto.getExporter())
                .orElseThrow(() -> new IllegalArgumentException("Exporter country not found: " + tariffMappingDto.getExporter())));

        tariffMapping.setTariffMappingID(id);
        return tariffMappingRepository.save(tariffMapping);
    }
  
}
