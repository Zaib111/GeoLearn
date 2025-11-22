package app.use_cases.compare;

import app.entities.Country;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Interactor for the Compare Countries use case.
 * <p>
 * Validates the requested comparison, looks up the corresponding Country
 * entities from the data access layer, and passes them to the presenter.
 */
public class CompareInteractor implements CompareInputBoundary {

    private final CompareDataAccessInterface countryDataAccess;
    private final CompareOutputBoundary presenter;

    public CompareInteractor(CompareDataAccessInterface countryDataAccess,
                             CompareOutputBoundary presenter) {
        this.countryDataAccess = countryDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(List<String> selectedCountryNames) {
        // Basic validation: need at least two countries
        if (selectedCountryNames == null || selectedCountryNames.size() < 2) {
            presenter.prepareFailView("Please select at least two countries to compare.");
            return;
        }

        // Check for duplicates while preserving order
        Map<String, Boolean> seen = new LinkedHashMap<>();
        for (String name : selectedCountryNames) {
            if (seen.containsKey(name)) {
                presenter.prepareFailView("Each selected country must be unique.");
                return;
            }
            seen.put(name, Boolean.TRUE);
        }

        // Load all countries from the data access layer
        List<Country> allCountries = countryDataAccess.getCountries();
        Map<String, Country> countriesByName = new LinkedHashMap<>();

        for (Country country : allCountries) {
            String name = country.getName();
            if (name != null && !name.isEmpty()) {
                // Later entries with the same name overwrite earlier ones, which is fine here
                countriesByName.put(name, country);
            }
        }

        // Resolve the user's selected names into Country entities
        List<Country> selectedCountries = new ArrayList<>();
        for (String name : selectedCountryNames) {
            Country match = countriesByName.get(name);
            if (match == null) {
                presenter.prepareFailView("Could not find data for country: " + name);
                return;
            }
            selectedCountries.add(match);
        }

        // Success: pass data to presenter
        CompareOutputData outputData = new CompareOutputData(selectedCountries);
        presenter.prepareSuccessView(outputData);
    }
}
