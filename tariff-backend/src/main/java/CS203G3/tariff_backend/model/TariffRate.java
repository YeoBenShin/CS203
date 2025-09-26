package CS203G3.tariff_backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class TariffRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long TariffRateID;

    @ManyToOne
    @JoinColumn(name = "tariff_id", referencedColumnName = "tariff_id")
    private Tariff tariff;

    @OneToOne
    @JoinColumn(name = "metric_id", referencedColumnName = "metric_id")
    private MetricType metricType;

    @Column(name = "tariff_rate")
    private BigDecimal tariffRate;

    public TariffRate() {
    }

    public TariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }

    public Long getTariffRateID() {
        return TariffRateID;
    }

    public void setTariffRateID(Long tariffRateID) {
        TariffRateID = tariffRateID;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public BigDecimal getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }
    
}
