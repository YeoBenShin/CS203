package CS203G3.tariff_backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import CS203G3.tariff_backend.service.ProductService;
import CS203G3.tariff_backend.model.Product;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Get all products
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }


    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GetMapping("/{hsCode}")
    public ResponseEntity<Product> getProductByHsCode(@PathVariable Integer hsCode) {
        Product product = productService.getProductByHsCode(hsCode);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }


    /**
     * Create a new product
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {    
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Update an existing product
     * PUT /api/products/{id}
     */
    @PutMapping("/{hsCode}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer hsCode, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(hsCode, product);
        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete a product
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{hsCode}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer hsCode) {
        productService.deleteProduct(hsCode);
        return ResponseEntity.noContent().build();
    }
}
