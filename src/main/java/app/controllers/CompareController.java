package app.controllers;

import app.use_cases.compare.CompareInputBoundary;

import java.util.List;

public class CompareController {

    private final CompareInputBoundary compareUseCase;

    public CompareController(CompareInputBoundary compareUseCase) {
        this.compareUseCase = compareUseCase;
    }

    public void compareCountries(List<String> selectedCountryNames) {
        compareUseCase.execute(selectedCountryNames);
    }
}
