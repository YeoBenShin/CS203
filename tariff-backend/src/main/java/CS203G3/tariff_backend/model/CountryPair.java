package CS203G3.tariff_backend.model;

import jakarta.persistence.*;
import java.util.*;


@Entity
@Table(name = "country_pairs")
public class CountryPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exporter_id", nullable = false)
    private Country exporter;

    @ManyToOne
    @JoinColumn(name = "importer_id", nullable = false)
    private Country importer;

    @OneToMany(mappedBy = "countryPair", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tariff> tariffs;

    // Constructors
    public CountryPair() {}

    public CountryPair(Country exporter, Country importer) {
        this.exporter = exporter;
        this.importer = importer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
