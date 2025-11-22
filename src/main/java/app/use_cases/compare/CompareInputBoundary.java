package app.use_cases.compare;

import java.util.List;

/**
 * The CompareInputBoundary interface defines the input methods for the country comparison use case.
 *
 * <p>
 * Implementations should handle loading available countries and executing a comparison based on selected country names.
 */
public interface CompareInputBoundary {
    /**
     * Loads the available countries for comparison.
     */
    void loadAvailableCountries();

    /**
     * Executes the comparison for the selected country names.
     * @param selectedCountryNames the names of the countries to compare
     */
    void execute(List<String> selectedCountryNames);
}
