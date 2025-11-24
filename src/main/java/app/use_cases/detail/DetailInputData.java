package app.use_cases.detail;

public class DetailInputData {
    private final String countryName;
    public DetailInputData(String countryName) {
        this.countryName = countryName;
    }

    String getCountryName() {
        return countryName;
    }
}
