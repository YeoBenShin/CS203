package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.service.ProductMetricService;
import CS203G3.tariff_backend.dto.ProductMetricDto;
import CS203G3.tariff_backend.dto.ProductMetricCreateDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/productmetrics")
public class ProductMetricController {
    private final ProductMetricService productMetricService;

    public ProductMetricController(ProductMetricService productMetricService) {
        this.productMetricService = productMetricService;
    }
    
    /**
     * Get all product metrics
     * GET /api/productmetrics
     */
    @GetMapping
    public ResponseEntity<List<ProductMetricDto>> getAllProductMetrics() {
        List<ProductMetricDto> productMetrics = productMetricService.getAllProductMetrics();
        return ResponseEntity.ok(productMetrics);
    }


    /**
     * Get productmetrics for each product via its hSCode
     * GET /api/productmetrics/{hSCode}
     */
    @GetMapping("/{hSCode}")
    public ResponseEntity<List<ProductMetricDto>> getProductByHSCode(@PathVariable String hSCode) {
        List<ProductMetricDto> productMetrics = productMetricService.getProductMetricByHSCode(hSCode);
        if (productMetrics == null || productMetrics.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMetrics);
    }

    /**
     * Get product metric by ID
     * GET /api/productmetrics/metric/{id}
     */
    @GetMapping("/metric/{id}")
    public ResponseEntity<ProductMetricDto> getProductMetricById(@PathVariable Long id) {
        ProductMetricDto productMetric = productMetricService.getProductMetricById(id);
        return ResponseEntity.ok(productMetric);
    }


    /**
     * Create a new product metric
     * POST /api/productmetrics
     */
    @PostMapping
    public ResponseEntity<ProductMetricDto> createProductMetric(@RequestBody ProductMetricCreateDto productMetricCreateDto) {
        ProductMetricDto createdProductMetric = productMetricService.createProductMetric(productMetricCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductMetric);
    }

    /**
     * Update an existing product metric
     * PUT /api/productmetrics/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductMetricDto> updateProductMetric(@PathVariable Long id, @RequestBody ProductMetricDto productMetricDto) {
        ProductMetricDto updatedProductMetric = productMetricService.updateProductMetric(id, productMetricDto);
        return ResponseEntity.ok(updatedProductMetric);
    }

    /**
     * Delete a product metric
     * DELETE /api/productmetrics/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductMetric(@PathVariable Long id) {
        productMetricService.deleteProductMetric(id);
        return ResponseEntity.noContent().build();
    }

}

