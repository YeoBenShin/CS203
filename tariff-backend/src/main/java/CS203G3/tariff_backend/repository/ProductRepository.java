package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Additional custom query methods can be added here if needed
    Product findByDescription(String description);
}