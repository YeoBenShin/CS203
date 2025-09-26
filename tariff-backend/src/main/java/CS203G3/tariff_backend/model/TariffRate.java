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
    private Long TariffRateID;

    @ManyToOne
    @JoinColumn(name = "tariff_id", referencedColumnName = "tariff_id")
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(referencedColumnName = "unit_of_calculation")
    private UnitOfCalculation unitOfCalculation;

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

    public BigDecimal getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(BigDecimal tariffRate) {
        this.tariffRate = tariffRate;
    }

    public UnitOfCalculation getUnitOfCalculation() {
        return unitOfCalculation;
    }

    public void setUnitOfCalculation(UnitOfCalculation unitOfCalculation) {
        this.unitOfCalculation = unitOfCalculation;
    }
    
}
