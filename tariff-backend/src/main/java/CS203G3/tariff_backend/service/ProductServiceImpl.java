package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product findByDescription(String description) {
        Product product = productRepository.findByDescription(description);
        
        if (product == null) {
            throw new ResourceNotFoundException("Product", description);
        }
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductByhSCode(String hSCode) {
        return productRepository.findById(hSCode).orElseThrow(() -> new ResourceNotFoundException("Product", hSCode.toString()));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(String hSCode, Product product) {
        if (!productRepository.existsById(hSCode)) {
            throw new ResourceNotFoundException("Product", hSCode.toString());
        }
        product.setHSCode(hSCode);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String hSCode) {
        if (!productRepository.existsById(hSCode)) {
            throw new ResourceNotFoundException("Product", hSCode.toString());
        }
        productRepository.deleteById(hSCode);
    }
}
