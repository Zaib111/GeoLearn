package app.use_cases.compare;

import app.entities.Country;

import java.util.List;

/**
 * Interactor for the Compare Countries use case.
 * Handles business rules: validating input, fetching data, and formatting output.
 */
public class CompareInteractor implements CompareInputBoundary {

    private final CompareDataAccessInterface dataAccess;
    private final CompareOutputBoundary presenter;

    public CompareInteractor(CompareDataAccessInterface dataAccess,
                             CompareOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void loadAvailableCountries() {
        List<String> names = dataAccess.getAllCountryNames();
        if (names == null || names.isEmpty()) {
            presenter.prepareFailView("Failed to load country list.");
            return;
        }
        presenter.prepareCountriesList(names);
    }

    @Override
    public void execute(List<String> selectedNames) {
        if (selectedNames == null || selectedNames.size() < 2) {
            presenter.prepareFailView("Select at least two countries to compare.");
            return;
        }

        List<Country> selectedCountries = dataAccess.getCountriesByNames(selectedNames);

        if (selectedCountries.size() != selectedNames.size()) {
            presenter.prepareFailView("Some selected countries could not be found.");
            return;
        }

        presenter.prepareSuccessView(new CompareOutputData(selectedCountries));
    }
}
