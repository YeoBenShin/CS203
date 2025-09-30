package CS203G3.tariff_backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class TariffRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long tariffRateID;

    @ManyToOne
    @JoinColumn(name = "tariff_id", referencedColumnName = "tariff_id")
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(name = "unit_of_calculation", referencedColumnName = "unit_of_calculation")
    private ProductMetric productMetric;

    @Column(name = "tariff_rate", precision = 38, scale = 6)
    private BigDecimal tariffRate;

    public TariffRate() {
    }

    public TariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }

    public Long getTariffRateID() {
        return tariffRateID;
    }

    public void setTariffRateID(Long tariffRateID) {
        this.tariffRateID = tariffRateID;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public BigDecimal getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }

    public ProductMetric getProductMetric() {
        return productMetric;
    }

    public void setProductMetric(ProductMetric productMetric) {
        this.productMetric = productMetric;
    }
}
