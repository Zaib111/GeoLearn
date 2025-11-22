package app.use_cases.compare;

import java.util.List;

import app.entities.Country;

/**
 * The CompareDataAccessInterface provides data access methods for the country comparison use case.
 * Implementations of this interface should provide access to country names and country entities
 * required for comparison operations.
 */
public interface CompareDataAccessInterface {
    /**
     * Returns a list of all country names available for comparison.
     * @return list of country names
     */
    List<String> getAllCountryNames();

    /**
     * Returns a list of Country entities for the given country names.
     * @param names list of country names
     * @return list of Country entities
     */
    List<Country> getCountriesByNames(List<String> names);
}
