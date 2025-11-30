package app.use_cases.detail;

/**
 * Input data for detail use case.
 */
public class DetailInputData {
    private final String countryCode;

    /**
     * Constructor.
     * @param countryCode the country code
     */
    public DetailInputData(final String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Get the country code.
     * @return the country code
     */
    final String getCountryCode() {
        return countryCode;
    }
}
