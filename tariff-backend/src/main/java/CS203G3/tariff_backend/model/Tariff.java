package CS203G3.tariff_backend.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
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
    @JoinColumn(name = "country_pair_id", referencedColumnName = "id")
    private CountryPair countryPair;

    @ManyToOne
    @JoinColumn(name = "hs_code", referencedColumnName = "hs_code")
    private Product product;

    @Column(name = "tariff_name")
    private String tariffName;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "reference", length = 255)
    private String reference;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TariffRate> tariffRates;

    // Constructors
    public Tariff() {}

    public Tariff(Long tariffID, Product product, String tariffName, CountryPair countryPair, Date effectiveDate, Date expiryDate, String reference) {
        this.tariffID = tariffID;
        this.product = product;
        this.tariffName = tariffName;
        this.countryPair = countryPair;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public CountryPair getCountryPair() {
        return countryPair;
    }

    public void setCountryPair(CountryPair countryPair) {
        this.countryPair = countryPair;
    }


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


    @Override
    public String toString() {
        return "Tariff [tariffID=" + tariffID + ", product=" + product + ", tariffName=" + tariffName + ", countryPair="
                + countryPair + ", effectiveDate=" + effectiveDate + ", expiryDate=" + expiryDate + ", reference="
                + reference + "]";
    }



}
