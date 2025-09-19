package CS203G3.tariff_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import java.util.List;

@Entity
@Table(name = "tariff_mapping")
public class TariffMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tariff_mapping_id")
    private Long tariffMappingID;

    @ManyToOne
    @JoinColumn(name = "Exporter")
    private Country exporter;

    @ManyToOne
    @JoinColumn(name = "Importer")
    private Country importer;

    @ManyToOne
    @JoinColumn(name = "HSCode")
    private Product product;
    @OneToMany(mappedBy = "tariffMapping", cascade = CascadeType.ALL)
    private List<Tariff> tariffs;

    // Constructors
    public TariffMapping() {}

    public TariffMapping(Country exporter, Country importer, Product product) {
        this.exporter = exporter;
        this.importer = importer;
        this.product = product;
    }

    // Getters and Setters
    public Long getTariffMappingID() {
        return tariffMappingID;
    }

    public void setTariffMappingID(Long tariffMappingID) {
        this.tariffMappingID = tariffMappingID;
    }

    public Country getExporter() {
        return exporter;
    }

    public void setExporter(Country exporter) {
        this.exporter = exporter;
    }

    public Country getImporter() {
        return importer;
    }

    public void setImporter(Country importer) {
        this.importer = importer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "TariffMapping{" +
                "tariffMappingID=" + tariffMappingID +
                ", exporter='" + exporter.getIsoCode() + '\'' +
                ", importer='" + importer.getIsoCode() + '\'' +
                ", productId='" + product.getHsCode() + '\'' +
                '}';
    }
}