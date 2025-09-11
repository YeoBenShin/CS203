package CS203G3.tariff_backend;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TariffData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer tariffID;

    private Integer tariffMappingID;

    private Double rate;

    private Date effectiveDate;

    private Date expiryDate;

    private String reference;

    public Integer getTariffID() {
        return tariffID;
    }

    public void setTariffID(Integer tariffID) {
        this.tariffID = tariffID;
    }

    public Integer getTariffMappingID() {
        return tariffMappingID;
    }

    public void setTariffMappingID(Integer tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
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

}
