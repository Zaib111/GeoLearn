package app.use_cases.compare;

import java.util.List;

import app.entities.Country;

/**
 * Interactor for the Compare Countries use case.
 * Handles business rules: validating input, fetching data, and formatting output.
 * Implements the CompareInputBoundary interface.
 */
public class CompareInteractor implements CompareInputBoundary {
    /** Data access interface for retrieving country data. */
    private final CompareDataAccessInterface dataAccess;
    /** Output boundary for presenting results. */
    private final CompareOutputBoundary presenter;

    /**
     * Constructs a CompareInteractor with the given data access and presenter.
     * @param dataAccess the data access interface
     * @param presenter the output boundary presenter
     */
    public CompareInteractor(final CompareDataAccessInterface dataAccess,
                             final CompareOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Loads the available countries for comparison and passes them to the presenter.
     */
    @Override
    public void loadAvailableCountries() {
        final List<String> names = dataAccess.getAllCountryNames();
        boolean success = true;
        String failMessage = null;
        if (names == null || names.isEmpty()) {
            success = false;
            failMessage = "Failed to load country list.";
        }
        if (success) {
            presenter.prepareCountriesList(names);
        }
        else {
            presenter.prepareFailView(failMessage);
        }
    }

    /**
     * Executes the comparison for the selected country names.
     * Validates input and passes results or errors to the presenter.
     * @param selectedNames the names of the countries to compare
     */
    @Override
    public void execute(final List<String> selectedNames) {
        boolean success = true;
        String failMessage = null;
        List<Country> selectedCountries = null;
        if (selectedNames == null || selectedNames.size() < 2) {
            success = false;
            failMessage = "Select at least two countries to compare.";
        }
        else {
            selectedCountries = dataAccess.getCountriesByNames(selectedNames);
            if (selectedCountries.size() != selectedNames.size()) {
                success = false;
                failMessage = "Some selected countries could not be found.";
            }
        }
        if (success) {
            presenter.prepareSuccessView(new CompareOutputData(selectedCountries));
        }
        else {
            presenter.prepareFailView(failMessage);
        }
    }
}
