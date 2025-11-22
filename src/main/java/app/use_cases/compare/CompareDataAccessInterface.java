package app.use_cases.compare;

import app.entities.Country;

import java.util.List;

/**
 * Data access interface for the Compare Countries use case.
 * Implemented by data access classes (e.g., APICountryDataAccessObject)
 */
public interface CompareDataAccessInterface {

    /**
     * Returns a list of all countries available for comparison.
     *
     * @return list of Country entities
     */
    List<Country> getCountries();
}
