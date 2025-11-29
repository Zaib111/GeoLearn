package app.controllers;

import app.use_cases.filter_country.FilterCountriesInputBoundary;
import app.use_cases.filter_country.FilterCountriesInputData;

/**
 * Controller for the Filter Countries use case.
 */
public class FilterCountriesController {
    private final FilterCountriesInputBoundary filterCountriesInputBoundary;

    public FilterCountriesController(FilterCountriesInputBoundary filterCountriesInputBoundary) {
        this.filterCountriesInputBoundary = filterCountriesInputBoundary;
    }

    /**
     * Initiates the country filtering process using the given parameters.
     *
     * @param searchTerm the text to match country names against
     * @param region     the selected region filter
     * @param subregion  the selected subregion filter
     */
    public void filterCountries(String searchTerm, String region, String subregion) {
        // Create input data
        final FilterCountriesInputData inputData = new FilterCountriesInputData(searchTerm, region, subregion);

        // Call the use case
        filterCountriesInputBoundary.filterCountries(inputData);
    }
}
