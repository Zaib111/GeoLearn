package app.controllers;

import java.util.List;

import app.use_cases.compare.CompareInputBoundary;

/**
 * Controller for comparing countries. Handles user requests and delegates to the use case interactor.
 */
public class CompareController {

    /** The use case interactor for comparing countries. */
    private final CompareInputBoundary interactor;

    /**
     * Constructs a CompareController with the given interactor.
     * @param interactor the use case interactor
     */
    public CompareController(final CompareInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Loads the available countries for comparison.
     */
    public void loadAvailableCountries() {
        interactor.loadAvailableCountries();
    }

    /**
     * Compares the selected countries.
     * @param selectedCountryNames the names of the countries to compare
     */
    public void compareCountries(final List<String> selectedCountryNames) {
        interactor.execute(selectedCountryNames);
    }
}
