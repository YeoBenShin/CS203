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
    @JoinColumn(name = "hs_code", referencedColumnName = "hs_code")
    private Product product;

    @Column(name = "name")
    private String tariffName;

    @ManyToOne
    @JoinColumn(name = "exporter", referencedColumnName = "isoCode")
    private Country exporter;

    @Column(name = "tariff_val_1")
    private BigDecimal tariffVal1;

    @Column(name = "tariff_val_2")
    private BigDecimal tariffVal2;

    @Column(name = "ad_valorem_rate", precision = 10, scale = 4)
    private BigDecimal adValoremRate;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "reference", length = 255)
    private String reference;

    // Constructors
    public Tariff() {}

    public Tariff(Long tariffID, Product product, String tariffName, Country exporter, BigDecimal tariffVal1,
            BigDecimal tariffVal2, BigDecimal adValoremRate, Date effectiveDate, Date expiryDate, String reference) {
        this.tariffID = tariffID;
        this.product = product;
        this.tariffName = tariffName;
        this.exporter = exporter;
        this.tariffVal1 = tariffVal1;
        this.tariffVal2 = tariffVal2;
        this.adValoremRate = adValoremRate;
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

    public Product gethSCode() {
        return product;
    }

    public void sethSCode(Product product) {
        this.product = product;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public Country getExporter() {
        return exporter;
    }

    public void setExporter(Country exporter) {
        this.exporter = exporter;
    }

    public BigDecimal getTariffVal1() {
        return tariffVal1;
    }

    public void setTariffVal1(BigDecimal tariffVal1) {
        this.tariffVal1 = tariffVal1;
    }

    public BigDecimal getTariffVal2() {
        return tariffVal2;
    }

    public void setTariffVal2(BigDecimal tariffVal2) {
        this.tariffVal2 = tariffVal2;
    }

    public BigDecimal getAdValoremRate() {
        return adValoremRate;
    }

    public void setAdValoremRate(BigDecimal adValoremRate) {
        this.adValoremRate = adValoremRate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Tariff [tariffID=" + tariffID + ", product=" + product + ", tariffName=" + tariffName + ", exporter="
                + exporter + ", tariffVal1=" + tariffVal1 + ", tariffVal2=" + tariffVal2 + ", adValoremRate="
                + adValoremRate + ", effectiveDate=" + effectiveDate + ", expiryDate=" + expiryDate + ", reference="
                + reference + "]";
    }

}
