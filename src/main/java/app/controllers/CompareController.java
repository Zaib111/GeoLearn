package app.controllers;

import app.use_cases.compare.CompareInputBoundary;

import java.util.List;

public class CompareController {

    private final CompareInputBoundary interactor;

    public CompareController(CompareInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadAvailableCountries() {
        interactor.loadAvailableCountries();
    }

    public void compareCountries(List<String> selectedCountryNames) {
        interactor.execute(selectedCountryNames);
    }
}
