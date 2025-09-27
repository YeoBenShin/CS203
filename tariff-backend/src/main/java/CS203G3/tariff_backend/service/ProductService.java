package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Product;

public interface ProductService {

    Product findByDescription(String description);

    
}
