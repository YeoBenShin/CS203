package CS203G3.tariff_backend.service;

import java.util.List;
import CS203G3.tariff_backend.dto.ProductMetricDto;
import CS203G3.tariff_backend.dto.ProductMetricCreateDto;

public interface ProductMetricService {


    /**
     * Get all product metrics
     * @return List of all product metrics
     */
    List<ProductMetricDto> getAllProductMetrics();

    /**
     * Get product metric by ID
     * @param id The product metric ID
     * @return The product metric
     */
    ProductMetricDto getProductMetricById(Long id);

    /**
     * Get product metric by HS code
     * @param hSCode The product HS code
     * @return The product metric
     */
    List<ProductMetricDto> getProductMetricByHSCode(String hSCode);

    /**
     * Create a new product metric with business validation
     * @param productMetricCreateDto The product metric to create
     * @return The created product metric
     */
    ProductMetricDto createProductMetric(ProductMetricCreateDto productMetricCreateDto);

    /**
     * Update an existing product metric
     * @param productMetricId The product metric ID
     * @param productMetricDto The product metric data to update
     * @return The updated product metric
     */
    ProductMetricDto updateProductMetric(Long productMetricId, ProductMetricDto productMetricDto);

    /**
     * Delete a product metric
     * @param productMetricId The product metric ID to delete
     */
    void deleteProductMetric(Long productMetricId);

}
