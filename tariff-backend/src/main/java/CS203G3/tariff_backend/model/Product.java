package CS203G3.tariff_backend.model;

public class Product {
    private String HSCode;
    private String Description;

    public Product() {
    }

    public Product(String description) {
        this.Description = description;
    }

    public Product(String HSCode, String description) {
        this.HSCode = HSCode;
        this.Description = description;
    }

    public String getHSCode() {
        return HSCode;
    }

    public void setHSCode(String HSCode) {
        this.HSCode = HSCode;
    }

    public String getDescription() {
        return Description;
    }

    public String getName() {
        return Description;
    }
    
    public void setDescription(String description) {
        Description = description;
    }
}
