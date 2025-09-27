package CS203G3.tariff_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CS203G3.tariff_backend.dto.TariffMappingCreateDto;
import CS203G3.tariff_backend.dto.TariffMappingDto;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.exception.TariffMappingAlreadyExistsException;
import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.repository.CountryRepository;
import CS203G3.tariff_backend.repository.ProductRepository;
import CS203G3.tariff_backend.repository.TariffMappingRepository;

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
        dto.setHSCode(tariffMapping.getProduct().getHsCode());
        dto.setImporter(tariffMapping.getImporter().getIsoCode());
        dto.setExporter(tariffMapping.getExporter().getIsoCode());
        dto.setProductDescription(tariffMapping.getProduct().getDescription());
        return dto;
    }

    public TariffMapping convertToEntity(TariffMappingCreateDto dto) {
        TariffMapping tariffMapping = new TariffMapping();
        // System.out.println("Product ID: " + dto.getHSCode());
        // System.out.println("Importer ID: " + dto.getImporter());
        // System.out.println("Exporter ID: " + dto.getExporter());
        tariffMapping.setProduct(productRepository.findById(dto.getHSCode()).orElseThrow(() -> new ResourceNotFoundException("Product", dto.getHSCode().toString())));
        tariffMapping.setImporter(countryRepository.findById(dto.getImporter()).orElseThrow(() -> new ResourceNotFoundException("Country", dto.getImporter())));
        tariffMapping.setExporter(countryRepository.findById(dto.getExporter()).orElseThrow(() -> new ResourceNotFoundException("Country", dto.getExporter())));
        return tariffMapping;
    }

    @Override
    @Transactional
    public TariffMappingDto createTariffMapping(TariffMappingCreateDto tariffMappingCreateDto) {
        // check that if tariffmapping exists, dont create tariffmapping
        TariffMapping existing = tariffMappingRepository.findByProduct_HsCodeAndImporter_IsoCodeAndExporter_IsoCode(
            tariffMappingCreateDto.getHSCode(),
            tariffMappingCreateDto.getImporter(),
            tariffMappingCreateDto.getExporter()
        );

        if (existing != null) {
            throw new TariffMappingAlreadyExistsException(
                tariffMappingCreateDto.getHSCode(),
                tariffMappingCreateDto.getImporter(),
                tariffMappingCreateDto.getExporter()
            );
        }

        TariffMapping entity = convertToEntity(tariffMappingCreateDto);
        TariffMapping saved = tariffMappingRepository.save(entity);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public void deleteTariffMapping(Long id) {
        if (!tariffMappingRepository.existsById(id)) {
            throw new ResourceNotFoundException("TariffMapping", id.toString());
        }
        
        tariffMappingRepository.deleteById(id);
    }

    @Override
    public List<TariffMappingDto> getAllTariffMappings() {
        List<TariffMapping> tariffMappings = tariffMappingRepository.findAll();

        return tariffMappings.stream().map(this::convertToDto).toList();
    }

    @Override
    public TariffMappingDto getTariffMappingById(Long id) {
        
        TariffMapping entity = tariffMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TariffMapping", id.toString()));

        return convertToDto(entity);
    }
    @Override
    @Transactional
    public TariffMappingDto updateTariffMapping(Long id, TariffMappingDto tariffMappingDto) {
        if (!tariffMappingRepository.existsById(id)) {
            throw new ResourceNotFoundException("TariffMapping", id.toString());
        }

        TariffMapping tariffMapping = tariffMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TariffMapping", id.toString()));

        // Update entity fields from DTO
        tariffMapping.setProduct(productRepository.findById(tariffMappingDto.getHSCode())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + tariffMappingDto.getHSCode())));
        
        tariffMapping.setImporter(countryRepository.findById(tariffMappingDto.getImporter())
                .orElseThrow(() -> new IllegalArgumentException("Importer country not found: " + tariffMappingDto.getImporter())));

        tariffMapping.setExporter(countryRepository.findById(tariffMappingDto.getExporter())
                .orElseThrow(() -> new IllegalArgumentException("Exporter country not found: " + tariffMappingDto.getExporter())));

        // Save the entity (not the DTO)
        TariffMapping savedMapping = tariffMappingRepository.save(tariffMapping);
        
        // Convert the saved entity back to DTO and return it
        return convertToDto(savedMapping);
    }
  
}
