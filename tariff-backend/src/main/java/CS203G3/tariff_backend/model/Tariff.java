package CS203G3.tariff_backend.model;

import java.sql.Date;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "tariff")
public class Tariff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tariff_id")
    private Long tariffID;

    @ManyToOne
    @JoinColumn(name = "tariff_mapping_id")
    private TariffMapping tariffMapping;

    @Column(name = "rate", precision = 10, scale = 4)
    private BigDecimal rate;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "reference", length = 255)
    private String reference;

    // Constructors
    public Tariff() {}

    public Tariff(TariffMapping tariffMapping, BigDecimal rate, Date effectiveDate, Date expiryDate, String reference) {
        this.tariffMapping = tariffMapping;
        this.rate = rate;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.reference = reference;
    }

    // Getters and Setters
    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
    }

    public TariffMapping getTariffMapping() {
        return tariffMapping;
    }

    public void setTariffMapping(TariffMapping tariffMapping) {
        this.tariffMapping = tariffMapping;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "tariffID=" + tariffID +
                ", tariffMapping=" + tariffMapping +
                ", rate=" + rate +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                ", reference='" + reference + '\'' +
                '}';
    }
}
