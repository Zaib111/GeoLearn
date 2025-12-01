package app.use_cases.detail;

public class DetailInputData {
    private final String countryInfo;
    public DetailInputData(String countryInfo) {
        this.countryInfo = countryInfo;
    }

    String getCountryInfo() {
        return countryInfo;
    }
}
