package app.controllers;

import app.use_cases.filter_country.FilterCountriesInputBoundary;
import app.use_cases.filter_country.FilterCountriesInputData;

public class FilterCountriesController {
    private final FilterCountriesInputBoundary filterCountriesInputBoundary;

    public FilterCountriesController(FilterCountriesInputBoundary filterCountriesInputBoundary) {
        this.filterCountriesInputBoundary = filterCountriesInputBoundary;
    }

    public void filterCountries(String searchTerm, String region, String subregion) {
        // Create input data
        FilterCountriesInputData inputData = new FilterCountriesInputData(searchTerm, region, subregion);

        // Call the use case
        filterCountriesInputBoundary.filterCountries(inputData);
    }

    // Method for displaying the details of the clicked Country
    public void openCountryDetails(String countryName) { filterCountriesInputBoundary.openCountryDetails(countryName); }
}
