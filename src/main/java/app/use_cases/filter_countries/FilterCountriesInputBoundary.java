package app.use_cases.filter_countries;

/**
 * Input boundary for the Filter Countries use case, which requests the system filter countries
 * according to set criteria.
 */
public interface FilterCountriesInputBoundary {
    /**
     * Executes the filter operation using the provided input data.
     *
     * @param inputData the search criteria (search term, region, subregion)
     */
    void filterCountries(FilterCountriesInputData inputData);
}
