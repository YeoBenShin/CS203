package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

@Entity
public class ProductMetric {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productMetricID;

    @ManyToOne
    @JoinColumn(name = "hs_code", referencedColumnName = "hs_code")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_calculation")
    private UnitOfCalculation unitOfCalculation;

    public ProductMetric() {
    }

    public ProductMetric(Long productMetricID, Product product, UnitOfCalculation unitOfCalculation) {
        this.productMetricID = productMetricID;
        this.product = product;
        this.unitOfCalculation = unitOfCalculation;
    }

    public Long getProductMetricID() {
        return productMetricID;
    }

    public void setProductMetricID(Long productMetricID) {
        this.productMetricID = productMetricID;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
}
