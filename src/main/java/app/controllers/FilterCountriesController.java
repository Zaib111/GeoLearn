package app.controllers;

import app.use_cases.filter_country.FilterCountriesInputBoundary;
import app.use_cases.filter_country.FilterCountriesInputData;

/**
 * Controller that adapts UI input into the filter-countries use case boundary.
 * It builds the input data object and forwards it to the input boundary.
 */
public class FilterCountriesController {
    private final FilterCountriesInputBoundary filterCountriesInputBoundary;

    /**
     * Create a new FilterCountriesController.
     *
     * @param filterCountriesInputBoundary the use-case input boundary used to filter countries
     */
    public FilterCountriesController(FilterCountriesInputBoundary filterCountriesInputBoundary) {
        this.filterCountriesInputBoundary = filterCountriesInputBoundary;
    }

    /**
     * Filter countries using the given search parameters.
     *
     * @param searchTerm substring to match against country names (may be null or empty)
     * @param region     region name to filter by (may be null or empty)
     * @param subregion  subregion name to filter by (may be null or empty)
     */
    public void filterCountries(String searchTerm, String region, String subregion) {
        // Create input data
        final FilterCountriesInputData inputData = new FilterCountriesInputData(searchTerm, region, subregion);

        // Call the use case
        filterCountriesInputBoundary.filterCountries(inputData);
    }
}
