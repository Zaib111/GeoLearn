package app.use_cases.detail;

public class DetailInputData {
    private final String countryCode;
    public DetailInputData(String countryCode) {
        this.countryCode = countryCode;
    }

    String getCountryCode() {
        return countryCode;
    }
}
