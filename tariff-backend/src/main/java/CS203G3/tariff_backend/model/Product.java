package CS203G3.tariff_backend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @Column(name = "hs_code")
    private Integer hSCode;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "rate_type")
    private String rateType;

    @Column(name = "qty_unit_1")
    private String qtyUnit1;

    @Column(name = "qty_unit_2")
    private String qtyUnit2;

    @Column(name = "ad_valorem")
    private Boolean adValorem;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tariff> tariffs;

    // Constructors
    public Product() {}
    
    public Product(Integer hSCode, String description, String rateType, String qtyUnit1, String qtyUnit2,
            Boolean adValorem) {
        this.hSCode = hSCode;
        this.description = description;
        this.rateType = rateType;
        this.qtyUnit1 = qtyUnit1;
        this.qtyUnit2 = qtyUnit2;
        this.adValorem = adValorem;
    }

    // Getters and Setters
    public Integer getHsCode() {
        return hSCode;
    }
    
    public void setHsCode(Integer hsCode) {
        this.hSCode = hsCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer gethSCode() {
        return hSCode;
    }

    public void sethSCode(Integer hSCode) {
        this.hSCode = hSCode;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getQtyUnit1() {
        return qtyUnit1;
    }

    public void setQtyUnit1(String qtyUnit1) {
        this.qtyUnit1 = qtyUnit1;
    }

    public String getQtyUnit2() {
        return qtyUnit2;
    }

    public void setQtyUnit2(String qtyUnit2) {
        this.qtyUnit2 = qtyUnit2;
    }

    public Boolean getAdValorem() {
        return adValorem;
    }

    public void setAdValorem(Boolean adValorem) {
        this.adValorem = adValorem;
    }

    @Override
    public String toString() {
        return "Product{" +
                "hsCode='" + hSCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}