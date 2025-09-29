package CS203G3.tariff_backend.service;

import java.util.List;

import CS203G3.tariff_backend.model.Product;

public interface ProductService {

    /**
     * Find product by description
     * @param description The product description
     * @return The product
     */
    Product findByDescription(String description);

    /**
     * Get all products
     * @return List of all products
     */
    List<Product> getAllProducts();

    /**
     * Get product by HS code
     * @param hSCode The product HS code
     * @return The product
     */
    Product getProductByhSCode(String hSCode);

    /**
     * Create a new product with business validation
     * @param product The product to create
     * @return The created product
     */
    Product createProduct(Product product);

    /**
     * Update an existing product
     * @param hSCode The product HS code
     * @param product The product data to update
     * @return The updated product
     */
    Product updateProduct(String hSCode, Product product);

    /**
     * Delete a product
     * @param hSCode The product HS code to delete
     */
    void deleteProduct(String hSCode);
    
}
