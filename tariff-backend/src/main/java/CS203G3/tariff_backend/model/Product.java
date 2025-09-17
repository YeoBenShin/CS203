package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @Column(name = "hs_code")
    private Integer hsCode; // Changed to Integer to match database INT
    
    @Column(name = "description", length = 255)
    private String description;
    
    // Constructors
    public Product() {}
    
    public Product(Integer hsCode, String description) {
        this.hsCode = hsCode;
        this.description = description;
    }
    
    // Getters and Setters
    public Integer getHsCode() {
        return hsCode;
    }
    
    public void setHsCode(Integer hsCode) {
        this.hsCode = hsCode;
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
                "hsCode='" + hsCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}