package CS203G3.tariff_backend.service;

import java.util.List;

import CS203G3.tariff_backend.model.Product;

public interface ProductService {
    /**
     * Get all products
     * @return List of all products
     */
    List<Product> getAllProducts();

    /**
     * Get product by HS code
     * @param hsCode The product HS code
     * @return The product
     */
    Product getProductByHsCode(Integer hsCode);

    /**
     * Create a new product with business validation
     * @param product The product to create
     * @return The created product
     */
    Product createProduct(Product product);

    /**
     * Update an existing product
     * @param hsCode The product HS code
     * @param product The product data to update
     * @return The updated product
     */
    Product updateProduct(Integer hsCode, Product product);

    /**
     * Delete a country
     * @param hsCode The product HS code to delete
     */
    void deleteProduct(Integer hsCode);
}
