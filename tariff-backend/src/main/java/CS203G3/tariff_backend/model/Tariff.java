package CS203G3.tariff_backend.model;


import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;


public class Tariff {
    private static final AtomicLong counter = new AtomicLong();
    private Long TariffID;
    private String Exporter;
    private String Importer;
    private String ProductID;
    private Double TariffRate;
    private Date EffectiveDate;
    private Date ExpiryDate;

    public Tariff() {

    }

    public Tariff(String exporter, String importer, String productID, Double tariffRate, Date effectiveDate, Date expiryDate) {
        this.TariffID = counter.incrementAndGet();
        this.Exporter = exporter;
        this.Importer = importer;
        this.ProductID = productID;
        this.TariffRate = tariffRate;
        this.EffectiveDate = effectiveDate;
        this.ExpiryDate = expiryDate;
    }

    public static AtomicLong getCounter() {
        return counter;
    }

    public Long getTariffID() {
        return TariffID;
    }

    public void setTariffID(Long tariffID) {
        TariffID = tariffID;
    }

    public String getExporter() {
        return Exporter;
    }

    public void setExporter(String exporter) {
        Exporter = exporter;
    }

    public String getImporter() {
        return Importer;
    }

    public void setImporter(String importer) {
        Importer = importer;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public Double getTariffRate() {
        return TariffRate;
    }

    public void setTariffRate(Double tariffRate) {
        TariffRate = tariffRate;
    }

    public Date getEffectiveDate() {
        return EffectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        EffectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        ExpiryDate = expiryDate;
    }

    
}
