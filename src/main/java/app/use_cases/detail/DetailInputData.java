package app.use_cases.detail;

/**
 * Data structure representing the input data required for the Detail Use Case.
 * Encapsulates the information provided by the user (or previous view) necessary to identify a country.
 */
public class DetailInputData {
    // Stores the raw country identifier, which could be a name or a code
    private final String countryInfo;

    /**
     * Constructor for the input data.
     *
     * @param countryInfo The string identifier for the country (e.g., "Canada" or "CA").
     */
    public DetailInputData(String countryInfo) {
        this.countryInfo = countryInfo;
    }

    /**
     * Retrieves the stored country information string.
     *
     * @return The country identifier.
     */
    String getCountryInfo() {
        return countryInfo;
    }
}