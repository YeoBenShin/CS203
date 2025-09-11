package CS203G3.tariff_backend.model;

public class Country {
    private String CountryID;
    private String name;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(String countryID, String name) {
        this.CountryID = countryID;
        this.name = name;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String countryID) {
        CountryID = countryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
