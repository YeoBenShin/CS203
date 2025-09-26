package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

@Entity
public class MetricType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long metricID;

    @Column
    private String unitOfCalculation;

    // Constructors
    public MetricType() {
    }

    public MetricType(Long metricID, String unitOfCalculation) {
        this.metricID = metricID;
        this.unitOfCalculation = unitOfCalculation;
    }

    // Getters and Setters
    public Long getMetricID() {
        return metricID;
    }

    public void setMetricID(Long metricID) {
        this.metricID = metricID;
    }

    public String getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(String unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
}
