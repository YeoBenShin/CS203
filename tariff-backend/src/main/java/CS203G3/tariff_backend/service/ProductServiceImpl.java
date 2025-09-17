package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductByHsCode(Integer hsCode) {
        return productRepository.findById(hsCode).orElse(null);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer hsCode, Product product) {
        if (!productRepository.existsById(hsCode)) {
            return null;
        }
        product.setHsCode(hsCode);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer hsCode) {
        productRepository.deleteById(hsCode);
    }
}
