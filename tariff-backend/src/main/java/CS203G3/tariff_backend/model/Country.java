package CS203G3.tariff_backend.model;

import jakarta.persistence.*;
import java.util.*;


@Entity
public class Country {
    
    @Id
    @Column(name = "iso_code", length = 3)
    private String isoCode; // SGP, USA, etc.

    @Column(name = "name", nullable = false, length = 255)
    private String name; // Singapore, United States, etc.

    @Column(name = "region", length = 255)
    private String region;

    @OneToMany(mappedBy = "exporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CountryPair> exportCountries;

    @OneToMany(mappedBy = "importer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CountryPair> importCountries;

    // Constructors
    public Country() {}
    
    public Country(String isoCode, String name, String region) {
        this.isoCode = isoCode;
        this.name = name;
        this.region = region;
    }
    
    // Getters and Setters
    public String getIsoCode() {
        return isoCode;
    }
    
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    @Override
    public String toString() {
        return "Country{" +
                "isoCode='" + isoCode + '\'' +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}