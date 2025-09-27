package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.repository.ProductRepository;

import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product findByDescription(String descripion) {
        return productRepository.findByDescription(descripion);
    }
}
