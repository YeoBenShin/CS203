package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.service.ProductService;
import CS203G3.tariff_backend.model.Product;
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
import org.springframework.beans.factory.annotation.Autowired;


@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{description}")
    public Product gethSCode(@PathVariable String description) {
        return productService.findByDescription(description);
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
     * Get product by HS Code
     * GET /api/products/{hSCode}
     */
    @GetMapping("/hsCode/{hSCode}")
    public ResponseEntity<Product> getProductByhSCode(@PathVariable Integer hSCode) {
        Product product = productService.getProductByhSCode(hSCode);
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
     * PUT /api/products/{hSCode}
     */
    @PutMapping("/{hSCode}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer hSCode, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(hSCode, product);
        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete a product
     * DELETE /api/products/{hSCode}
     */
    @DeleteMapping("/{hSCode}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer hSCode) {
        productService.deleteProduct(hSCode);
        return ResponseEntity.noContent().build();
    }

}
