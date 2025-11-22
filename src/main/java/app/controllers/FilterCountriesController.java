package app.controllers;

import app.use_cases.filter_country.FilterCountriesInputBoundary;
import app.use_cases.filter_country.FilterCountriesInputData;

public class FilterCountriesController {
    private final FilterCountriesInputBoundary interactor;

    public FilterCountriesController(FilterCountriesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void filterCountries(String searchTerm, String region, String subregion) {
        // Create input data
        FilterCountriesInputData inputData = new FilterCountriesInputData(searchTerm, region, subregion);

        // Call the use case
        interactor.execute(inputData);
    }
}
