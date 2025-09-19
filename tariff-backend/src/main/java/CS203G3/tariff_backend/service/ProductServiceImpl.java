package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductRepository;
import CS203G3.tariff_backend.exception.ProductNotFoundException;
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
        return productRepository.findById(hsCode).orElseThrow(() -> new ProductNotFoundException(hsCode));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer hsCode, Product product) throws ProductNotFoundException{
        if (!productRepository.existsById(hsCode)) {
            throw new ProductNotFoundException(hsCode);
        }
        product.setHsCode(hsCode);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer hsCode) throws ProductNotFoundException {
        if (!productRepository.existsById(hsCode)) {
            throw new ProductNotFoundException(hsCode);
        }
        productRepository.deleteById(hsCode);
    }
}
