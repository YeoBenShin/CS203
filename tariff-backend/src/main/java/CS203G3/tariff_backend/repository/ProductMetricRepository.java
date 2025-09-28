package CS203G3.tariff_backend.repository;

import CS203G3.tariff_backend.model.ProductMetric;
import CS203G3.tariff_backend.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMetricRepository extends JpaRepository<ProductMetric, Long> {
    List<ProductMetric> findAllByProduct(Product product);
    
    @Query("SELECT pm FROM ProductMetric pm WHERE pm.product.hSCode = :hSCode")
    List<ProductMetric> findAllByProductHSCode(@Param("hSCode") Integer hSCode);
}
