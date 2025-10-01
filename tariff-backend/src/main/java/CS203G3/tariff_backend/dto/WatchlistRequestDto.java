package CS203G3.tariff_backend.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatchlistRequestDto {
    private Long tariffID;
    private Long tariffMappingID;
    private Long watchlistID;
    private String userID;
    
    // Core tariff fields
    private BigDecimal rate;
    private Date effectiveDate;  // JavaScript Date object
    private Date expiryDate;     // JavaScript Date object
    private String reference;
    
    // fields for frontend display from tariff mapping
    private String exporterCode;
    private String exporterName;
    private String importerCode;
    private String importerName;
    @JsonProperty("HSCode")
    private Integer HSCode;
    private String productDescription;

    // Constructors
    public WatchlistRequestDto() {}

    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
    }

    public Long getTariffMappingID() {
        return tariffMappingID;
    }

    public void setTariffMappingID(Long tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }

    public Long getWatchlistID() {
        return watchlistID;
    }

    public void setWatchlistID(Long watchlistID) {
        this.watchlistID = watchlistID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getExporterCode() {
        return exporterCode;
    }

    public void setExporterCode(String exporterCode) {
        this.exporterCode = exporterCode;
    }

    public String getExporterName() {
        return exporterName;
    }

    public void setExporterName(String exporterName) {
        this.exporterName = exporterName;
    }

    public String getImporterCode() {
        return importerCode;
    }

    public void setImporterCode(String importerCode) {
        this.importerCode = importerCode;
    }

    public String getImporterName() {
        return importerName;
    }

    public void setImporterName(String importerName) {
        this.importerName = importerName;
    }

    public Integer getHSCode() {
        return HSCode;
    }

    public void setHSCode(Integer hSCode) {
        HSCode = hSCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }


}
