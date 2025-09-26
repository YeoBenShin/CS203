package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

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
}
