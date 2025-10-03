package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @Column(name = "hs_code")
    private String hSCode;
    
    @Column(length = 255)
    private String description;

    // Constructors
    public Product() {}
    
    public Product(String hSCode, String description) {
        this.hSCode = hSCode;
        this.description = description;
    }

    // Getters and Setters
    public String getHSCode() {
        return hSCode;
    }

    public void setHSCode(String hSCode) {
        this.hSCode = hSCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "HSCode='" + hSCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}