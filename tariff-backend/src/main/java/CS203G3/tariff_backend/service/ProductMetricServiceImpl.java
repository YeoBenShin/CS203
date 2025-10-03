package CS203G3.tariff_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import CS203G3.tariff_backend.model.ProductMetric;
import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductMetricRepository;
import CS203G3.tariff_backend.repository.ProductRepository;
import CS203G3.tariff_backend.dto.ProductMetricCreateDto;
import CS203G3.tariff_backend.dto.ProductMetricDto;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;


@Service
public class ProductMetricServiceImpl implements ProductMetricService {

    private final ProductMetricRepository productMetricRepository;
    private final ProductRepository productRepository;

    public ProductMetricServiceImpl(ProductMetricRepository productMetricRepository, ProductRepository productRepository) {
        this.productMetricRepository = productMetricRepository;
        this.productRepository = productRepository;
    }

    public ProductMetricDto convertToDto(ProductMetric productMetric) {
        ProductMetricDto dto = new ProductMetricDto();
        dto.setId(productMetric.getProductMetricID());
        dto.setHSCode(productMetric.getProduct().getHSCode());
        dto.setDescription(productMetric.getProduct().getDescription());
        dto.setUnitOfCalculation(productMetric.getUnitOfCalculation());
        return dto;
    }


    public ProductMetric convertToEntity(ProductMetricCreateDto dto) {
        if (dto.getHSCode() == null) {
            throw new IllegalArgumentException("HSCode cannot be null");
        }
        
        ProductMetric productMetric = new ProductMetric();
        Product product = productRepository.findById(dto.getHSCode())
            .orElseThrow(() -> new ResourceNotFoundException("Product", dto.getHSCode().toString()));
        productMetric.setProduct(product);
        productMetric.setUnitOfCalculation(dto.getUnitOfCalculation());
        return productMetric;
    }

    @Override
    @Transactional
    public ProductMetricDto createProductMetric(ProductMetricCreateDto productMetricCreateDto) {
        ProductMetric productMetric = convertToEntity(productMetricCreateDto);
        ProductMetric savedProductMetric = productMetricRepository.save(productMetric);
        return convertToDto(savedProductMetric);
    }

    @Override
    @Transactional
    public void deleteProductMetric(Long productMetricId) {
        if (!productMetricRepository.existsById(productMetricId)) {
            throw new ResourceNotFoundException("ProductMetric", productMetricId.toString());
        }
        productMetricRepository.deleteById(productMetricId);
    }

    @Override
    public List<ProductMetricDto> getAllProductMetrics() {
        List<ProductMetric> productMetrics = productMetricRepository.findAll();
        return productMetrics.stream()
            .map(this::convertToDto)
            .toList();
    }

    @Override
    public ProductMetricDto getProductMetricById(Long id) {
        ProductMetric productMetric = productMetricRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductMetric", id.toString()));
        return convertToDto(productMetric);
    }

    @Override
    public List<ProductMetricDto> getProductMetricByHSCode(String hSCode) {
        List<ProductMetric> productMetrics = productMetricRepository.findAllByProductHSCode(hSCode);
        return productMetrics.stream()
            .map(this::convertToDto)
            .toList();
    }

    @Override
    @Transactional
    public ProductMetricDto updateProductMetric(Long productMetricId, ProductMetricDto productMetricDto) {
        ProductMetric existingProductMetric = productMetricRepository.findById(productMetricId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductMetric", productMetricId.toString()));

        Product product = productRepository.findById(productMetricDto.getHSCode())
            .orElseThrow(() -> new ResourceNotFoundException("Product", productMetricDto.getHSCode().toString()));
        
        existingProductMetric.setProduct(product);
        existingProductMetric.setUnitOfCalculation(productMetricDto.getUnitOfCalculation());

        ProductMetric updatedProductMetric = productMetricRepository.save(existingProductMetric);
        return convertToDto(updatedProductMetric);
    }
    
}
