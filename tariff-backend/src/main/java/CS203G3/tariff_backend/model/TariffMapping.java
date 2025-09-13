package CS203G3.tariff_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "tariff_mapping")
public class TariffMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tariff_mapping_id")
    private Long tariffMappingID;

    @Column(name = "exporter_iso_code", length = 45)
    private String exporter; // Just the isoCode like "SGP", "USA"

    @Column(name = "importer_iso_code", length = 45) 
    private String importer; // Just the isoCode like "SGP", "USA"

    @Column(name = "hs_code")
    private Integer productId; // Changed to Integer to match database INT

    // Constructors
    public TariffMapping() {}

    public TariffMapping(String exporter, String importer, Integer productId) {
        this.exporter = exporter;
        this.importer = importer;
        this.productId = productId;
    }

    // Getters and Setters
    public Long getTariffMappingID() {
        return tariffMappingID;
    }

    public void setTariffMappingID(Long tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "TariffMapping{" +
                "tariffMappingID=" + tariffMappingID +
                ", exporter='" + exporter + '\'' +
                ", importer='" + importer + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}