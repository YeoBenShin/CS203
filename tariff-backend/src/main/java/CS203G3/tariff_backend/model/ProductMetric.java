package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

@Entity
public class ProductMetric {
    @Id
    @Column
    private Long productMetricID;

    @ManyToOne
    @JoinColumn(name = "hs_code", referencedColumnName = "hs_code")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "metric_id", referencedColumnName = "metric_id")
    private MetricType metricType;

    public ProductMetric() {
    }

    public ProductMetric(Long productMetricID, Product product, MetricType metricType) {
        this.productMetricID = productMetricID;
        this.product = product;
        this.metricType = metricType;
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

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }
    
}
